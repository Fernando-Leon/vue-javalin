<template id="popular-books">
<app-frame>
  <v-sheet border rounded>
    <v-data-table
      :headers="headers"
      :hide-default-footer="books.length < 11"
      :items="books"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>
            <v-icon color="medium-emphasis" icon="mdi-book-multiple" size="x-small" start></v-icon>

            Popular books
          </v-toolbar-title>

          <v-btn
            class="me-2"
            prepend-icon="mdi-plus"
            rounded="lg"
            text="Add a Book"
            border
            @click="add"
          ></v-btn>
        </v-toolbar>
      </template>

      <template v-slot:item.title="{ value }">
        <v-chip :text="value" border="thin opacity-25" prepend-icon="mdi-book" label>
          <template v-slot:prepend>
            <v-icon color="medium-emphasis"></v-icon>
          </template>
        </v-chip>
      </template>

      <template v-slot:item.actions="{ item }">
        <div class="d-flex ga-2 justify-end">
          <v-icon color="medium-emphasis" icon="mdi-pencil" size="small" @click="edit(item.id)"></v-icon>

          <v-icon color="medium-emphasis" icon="mdi-delete" size="small" @click="remove(item.id)"></v-icon>
        </div>
      </template>

      <template v-slot:no-data>
        <v-btn
          prepend-icon="mdi-backup-restore"
          rounded="lg"
          text="Reset data"
          variant="text"
          border
          @click="initialize"
        ></v-btn>
      </template>
    </v-data-table>
  </v-sheet>

  <v-dialog v-model="dialog" max-width="500">
      <v-card
        :subtitle="isEditing ? 'Update' : 'Create' + ' your favorite book'"
        :title="isEditing ? 'Edit' : 'Add' + ' a Book'"
      >
      <template v-slot:text>
        <v-row>
          <v-col cols="12">
            <v-text-field v-model="record.title" label="Title"></v-text-field>
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field v-model="record.author" label="Author"></v-text-field>
          </v-col>

          <v-col cols="12" md="6">
            <v-select v-model="record.genre" :items="['Fiction', 'Dystopian', 'Non-Fiction', 'Sci-Fi']" label="Genre"></v-select>
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field v-model.number="record.year" type="number" :max="adapter.getYear(adapter.date())" :min="1" label="Year"></v-text-field>
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field v-model.number="record.pages" type="number" :min="1" label="Pages"></v-text-field>
          </v-col>
        </v-row>
      </template>

      <v-divider></v-divider>

      <v-card-actions class="bg-surface-light">
        <v-btn text="Cancel" variant="plain" @click="dialog = false"></v-btn>

        <v-spacer></v-spacer>

        <v-btn text="Save" @click="save"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  </app-frame>
</template>

<script>
app.component("popular-books", {
  template: "#popular-books",
  setup() {
    const { getCurrentInstance } = Vue;
    const instance = getCurrentInstance();
    const { $fetch, $toast, $showError, $swalConfirm } = instance.appContext.config.globalProperties;
    
    const adapter = Vuetify.useDate();
    
    const dialog = Vue.ref(false);
    const isEditing = Vue.ref(false);
    const books = Vue.ref([]);
    
    const headers = Vue.ref([
      { text: 'Title', value: 'title' },
      { text: 'Author', value: 'author' },
      { text: 'Genre', value: 'genre' },
      { text: 'Year', value: 'year' },
      { text: 'Pages', value: 'pages' },
      { text: 'Actions', value: 'actions', sortable: false }
    ]);
    
    const DEFAULT_RECORD = {
      title: '',
      author: '',
      genre: '',
      year: null,
      pages: null,
    };
    
    const record = Vue.ref({ ...DEFAULT_RECORD });
    
    const initialize = () => {
      dialog.value = false;
      $fetch('/api/books')
        .then(response => {
          books.value = response;
        })
        .catch(error => {
          $toast.fire({ icon: 'error', title: error });
        });
    };
    
    const add = () => {
      isEditing.value = false;
      record.value = { ...DEFAULT_RECORD };
      dialog.value = true;
    };
    
    const edit = (id) => {
      isEditing.value = true;
      const found = books.value.find((book) => book.id === id);
      if (found) {
        record.value = { ...found };
        dialog.value = true;
      }
    };
    
    const remove = (id) => {
      $swalConfirm(
        '¿Eliminar libro?',
        '¿Estás seguro de que quieres eliminar este libro? Esta acción no se puede deshacer.',
        'warning'
      ).then((result) => {
        if (result.isConfirmed) {
          $fetch(`/api/books/${id}`, {
            method: 'DELETE',
          })
          .then(() => {
            initialize();
            $toast.fire({ icon: 'success', title: 'Libro eliminado' });
          })
          .catch(err => {
            $showError('Error', 'No se pudo eliminar el libro');
          });
        }
      });
    };
    
    const save = () => {
      // Limpiar datos antes de enviar (sin incluir id en el body)
      const bookData = {
        title: record.value.title || '',
        author: record.value.author || '',
        genre: record.value.genre || '',
        year: record.value.year ? parseInt(record.value.year) : null,
        pages: record.value.pages ? parseInt(record.value.pages) : null,
      };
      
      if (isEditing.value) {
        // Para update: NO incluir el id en el body, solo en la URL
        $fetch(`/api/books/${record.value.id}`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(bookData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Libro actualizado' });
        })
        .catch(err => {
          console.error('Error al actualizar:', err);
          $showError('Error', 'No se pudo actualizar el libro: ' + err);
        });
      } else {
        // Para create: tampoco incluir id
        $fetch('/api/books', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(bookData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Libro agregado' });
        })
        .catch(err => {
          console.error('Error al crear:', err);
          let errorMessage = err.message;
          
          // Try to extract the validation message from the error response
          try {
            if (err.message && err.message.includes('REQUEST_BODY')) {
              const errorData = JSON.parse(err.message.split('Error: ')[1]);
              if (errorData.REQUEST_BODY && errorData.REQUEST_BODY.length > 0) {
                errorMessage = errorData.REQUEST_BODY[0].message;
              }
            }
          } catch (parseError) {
            // If parsing fails, use the original error message
            console.warn('Could not parse error message:', parseError);
          }
          
          $showError('Error', 'No se pudo crear el libro: ' + errorMessage);
        });
      }
    };
    
    Vue.onMounted(() => {
      initialize();
    });
    
    return {
      dialog,
      isEditing,
      books,
      headers,
      record,
      adapter,
      add,
      edit,
      remove,
      save,
      initialize
    };
  }
});
</script>