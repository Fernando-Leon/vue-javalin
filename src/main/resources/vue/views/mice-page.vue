<template id="mice-page">
<app-frame>
  <v-sheet border rounded>
    <v-toolbar flat>
      <v-toolbar-title>
        <v-icon color="medium-emphasis" icon="mdi-mouse" size="x-small" start></v-icon>
        Most Popular Mice
      </v-toolbar-title>

      <v-btn
        class="me-2"
        prepend-icon="mdi-plus"
        rounded="lg"
        text="Add a Mouse"
        border
        @click="add"
      ></v-btn>
    </v-toolbar>

  <v-data-iterator
    :items="mice"
    :items-per-page="itemsPerPage"
  >
    <template v-slot:header="{ page, pageCount, prevPage, nextPage }">
      <div class="d-flex justify-space-between mb-4 align-center px-4">
        <div class="d-flex align-center">
          <v-btn
            class="me-8"
            variant="text"
            @click="onClickSeeAll"
          >
            <span class="text-decoration-underline text-none">See all</span>
          </v-btn>

          <div class="d-inline-flex">
            <v-btn
              :disabled="page === 1"
              class="me-2"
              icon="mdi-arrow-left"
              size="small"
              variant="tonal"
              @click="prevPage"
            ></v-btn>

            <v-btn
              :disabled="page === pageCount"
              icon="mdi-arrow-right"
              size="small"
              variant="tonal"
              @click="nextPage"
            ></v-btn>
          </div>
        </div>
      </div>
    </template>

    <template v-slot:default="{ items }">
      <v-row>
        <v-col
          v-for="(item, i) in items"
          :key="i"
          cols="12"
          sm="6"
          xl="3"
        >
            <v-sheet border>
            <v-img
              :gradient="`to top right, rgba(255, 255, 255, .1), rgba(${getStaticColor(i)}, .15)`"
              :src="getStaticImage(i)"
              height="150"
              cover
            ></v-img>

            <v-list-item
              :title="item.raw.name"
              density="comfortable"
              lines="two"
              :subtitle="item.raw.description"
            >
              <template v-slot:title>
                <strong class="text-h6">
                  {{ item.raw.name }}
                </strong>
              </template>
            </v-list-item>

           <table class="text-caption" style="width: 100%; border-collapse: collapse;" density="compact">
             <tbody>
               <tr>
                 <th style="text-align: left; padding: 6px 12px; font-size: 1.1rem; font-weight: 600;">DPI:</th>
                 <td style="text-align: right; padding: 6px 12px; font-size: 1.1rem; font-weight: 400;">{{ item.raw.dpi }}</td>
               </tr>
               <tr>
                 <th style="text-align: left; padding: 6px 12px; font-size: 1.1rem; font-weight: 600;">Buttons:</th>
                 <td style="text-align: right; padding: 6px 12px; font-size: 1.1rem; font-weight: 400;">{{ item.raw.buttons }}</td>
               </tr>
               <tr>
                 <th style="text-align: left; padding: 6px 12px; font-size: 1.1rem; font-weight: 600;">Weight:</th>
                 <td style="text-align: right; padding: 6px 12px; font-size: 1.1rem; font-weight: 400;">{{ item.raw.weight }}g</td>
               </tr>
               <tr>
                 <th style="text-align: left; padding: 6px 12px; font-size: 1.1rem; font-weight: 600;">Wireless:</th>
                 <td style="text-align: right; padding: 6px 12px; font-size: 1.1rem; font-weight: 400;">{{ item.raw.wireless ? 'Yes' : 'No' }}</td>
               </tr>
               <tr>
                 <th style="text-align: left; padding: 6px 12px; font-size: 1.1rem; font-weight: 600;">Price:</th>
                 <td style="text-align: right; padding: 6px 12px; font-size: 1.1rem; font-weight: 400;">${{ item.raw.price }}</td>
               </tr>
             </tbody>
           </table>

           <v-card-actions>
             <v-spacer></v-spacer>
             <v-btn
               icon="mdi-pencil"
               size="small"
               variant="tonal"
               @click="edit(item.raw.id)"
             ></v-btn>
             <v-btn
               icon="mdi-delete"
               size="small"
               variant="tonal"
               color="error"
               @click="remove(item.raw.id)"
             ></v-btn>
           </v-card-actions>

          </v-sheet>
        </v-col>
      </v-row>
    </template>

    <template v-slot:footer="{ page, pageCount }">
      <v-footer
        class="justify-space-between text-body-2 mt-4"
        color="surface-variant"
      >
        Total mice: {{ mice.length }}

        <div>
          Page {{ page }} of {{ pageCount }}
        </div>
      </v-footer>
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
  </v-data-iterator>
  
  <!-- Dialog for Add/Edit Mouse -->
  <v-dialog v-model="dialog" max-width="600">
    <v-card
      :subtitle="isEditing ? 'Update' : 'Create' + ' your favorite mouse'"
      :title="isEditing ? 'Edit' : 'Add' + ' a Mouse'"
    >
    <template v-slot:text>
      <v-row>
        <v-col cols="12">
          <v-text-field v-model="record.name" label="Name" required></v-text-field>
        </v-col>

        <v-col cols="12">
          <v-textarea v-model="record.description" label="Description" required rows="2"></v-textarea>
        </v-col>

        <v-col cols="12" md="6">
          <v-text-field v-model.number="record.dpi" type="number" :min="1" label="DPI" required></v-text-field>
        </v-col>

        <v-col cols="12" md="6">
          <v-text-field v-model.number="record.buttons" type="number" :min="1" label="Buttons" required></v-text-field>
        </v-col>

        <v-col cols="12" md="6">
          <v-text-field v-model.number="record.weight" type="number" :min="1" label="Weight (g)" required></v-text-field>
        </v-col>

        <v-col cols="12" md="6">
          <v-text-field v-model.number="record.price" type="number" :min="0" step="0.01" label="Price ($)" required></v-text-field>
        </v-col>

        <v-col cols="12">
          <v-switch v-model="record.wireless" label="Wireless" color="primary"></v-switch>
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

  </v-sheet>
  </app-frame>
</template>

<script>
app.component("mice-page", {
  template: "#mice-page",
  setup() {
    const { getCurrentInstance } = Vue;
    const instance = getCurrentInstance();
    const { $fetch, $toast, $showError, $swalConfirm } = instance.appContext.config.globalProperties;
    
    const dialog = Vue.ref(false);
    const isEditing = Vue.ref(false);
    const mice = Vue.ref([]);
    const itemsPerPage = Vue.ref(4);
    
    // Imágenes estáticas que no se envían a la base de datos
    const staticImages = [
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/3.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/2.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/1.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/4.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/6.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/7.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/8.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/9.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/5.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/15.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/10.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/11.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/12.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/13.png',
      'https://cdn.vuetifyjs.com/docs/images/graphics/mice/14.png',
    ];
    
    // Colores estáticos para los gradientes
    const staticColors = [
      '14, 151, 210',
      '12, 146, 47',
      '107, 187, 226',
      '228, 196, 69',
      '156, 82, 251',
      '166, 39, 222',
      '131, 9, 10',
      '232, 94, 102',
      '58, 192, 239',
      '161, 252, 250',
      '201, 1, 2',
      '10, 181, 19',
      '100, 101, 102',
      '136, 241, 242',
      '109, 110, 114',
    ];
    
    const DEFAULT_RECORD = {
      name: '',
      description: '',
      dpi: null,
      buttons: null,
      weight: null,
      wireless: false,
      price: null,
    };
    
    const record = Vue.ref({ ...DEFAULT_RECORD });
    
    const getStaticImage = (index) => {
      return staticImages[index % staticImages.length];
    };
    
    const getStaticColor = (index) => {
      return staticColors[index % staticColors.length];
    };
    
    const initialize = () => {
      dialog.value = false;
      $fetch('/api/mice')
        .then(response => {
          mice.value = response;
        })
        .catch(error => {
          $toast.fire({ icon: 'error', title: 'Error loading mice: ' + error });
        });
    };
    
    const add = () => {
      isEditing.value = false;
      record.value = { ...DEFAULT_RECORD };
      dialog.value = true;
    };
    
    const edit = (id) => {
      isEditing.value = true;
      const found = mice.value.find((mouse) => mouse.id === id);
      if (found) {
        record.value = { ...found };
        dialog.value = true;
      }
    };
    
    const remove = (id) => {
      $swalConfirm(
        '¿Eliminar mouse?',
        '¿Estás seguro de que quieres eliminar este mouse? Esta acción no se puede deshacer.',
        'warning'
      ).then((result) => {
        if (result.isConfirmed) {
          $fetch(`/api/mice/${id}`, {
            method: 'DELETE',
          })
          .then(() => {
            initialize();
            $toast.fire({ icon: 'success', title: 'Mouse eliminado' });
          })
          .catch(err => {
            $showError('Error', 'No se pudo eliminar el mouse');
          });
        }
      });
    };
    
    const save = () => {
      // Limpiar datos antes de enviar (sin incluir id en el body)
      const mouseData = {
        name: record.value.name || '',
        description: record.value.description || '',
        dpi: record.value.dpi ? parseInt(record.value.dpi) : null,
        buttons: record.value.buttons ? parseInt(record.value.buttons) : null,
        weight: record.value.weight ? parseInt(record.value.weight) : null,
        wireless: !!record.value.wireless,
        price: record.value.price ? parseFloat(record.value.price) : null,
      };
      
      if (isEditing.value) {
        // Para update: NO incluir el id en el body, solo en la URL
        $fetch(`/api/mice/${record.value.id}`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(mouseData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Mouse actualizado' });
        })
        .catch(err => {
          console.error('Error al actualizar:', err);
          $showError('Error', 'No se pudo actualizar el mouse: ' + err);
        });
      } else {
        // Para create: tampoco incluir id
        $fetch('/api/mice', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(mouseData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Mouse agregado' });
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
          
          $showError('Error', 'No se pudo crear el mouse: ' + errorMessage);
        });
      }
    };

    const onClickSeeAll = () => {
      itemsPerPage.value = itemsPerPage.value === 4 ? mice.length : 4;
    };
    
    Vue.onMounted(() => {
      initialize();
    });
    
    return {
      dialog,
      isEditing,
      mice,
      record,
      itemsPerPage,
      add,
      edit,
      remove,
      save,
      initialize,
      onClickSeeAll,
      getStaticImage,
      getStaticColor
    };
  }
});
</script>


