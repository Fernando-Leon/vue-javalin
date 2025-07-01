<template id="dessert-page">
<app-frame>
  <v-container>
    <!-- Botón para agregar nuevo postre -->
    <v-row class="mb-4">
      <v-col>
        <v-btn 
          color="primary" 
          @click="add"
          prepend-icon="mdi-plus"
        >
          Agregar Postre
        </v-btn>
      </v-col>
    </v-row>

    <!-- Tabla de postres con diseño original -->
    <v-data-table
      :headers="headers"
      :items="desserts"
      :items-per-page="5"
      class="elevation-1"
    >
      <template v-slot:item.actions="{ item }">
        <v-btn
          icon="mdi-pencil"
          size="small"
          color="primary"
          @click="edit(item.id)"
          class="mr-2"
        ></v-btn>
        <v-btn
          icon="mdi-delete"
          size="small"
          color="error"
          @click="remove(item.id)"
        ></v-btn>
      </template>
    </v-data-table>

    <!-- Dialog para crear/editar postre -->
    <v-dialog v-model="dialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditing ? 'Editar Postre' : 'Nuevo Postre' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="record.name"
                  label="Nombre del postre"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.calories"
                  label="Calorías"
                  type="number"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.fat"
                  label="Grasa (g)"
                  type="number"
                  step="0.1"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.carbs"
                  label="Carbohidratos (g)"
                  type="number"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.protein"
                  label="Proteína (g)"
                  type="number"
                  step="0.1"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog = false">Cancelar</v-btn>
          <v-btn color="blue darken-1" text @click="save">Guardar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</app-frame>
</template>

<script>
app.component("dessert-page", {
  template: "#dessert-page",
  setup() {
    const { getCurrentInstance } = Vue;
    const instance = getCurrentInstance();
    const { $fetch, $toast, $showError, $swalConfirm } = instance.appContext.config.globalProperties;
    
    const dialog = Vue.ref(false);
    const isEditing = Vue.ref(false);
    const desserts = Vue.ref([]);
    
    const headers = Vue.ref([
      {
        text: 'Dessert (100g serving)',
        align: 'start',
        sortable: false,
        value: 'name',
      },
      { text: 'Calories', value: 'calories' },
      { text: 'Fat (g)', value: 'fat' },
      { text: 'Carbs (g)', value: 'carbs' },
      { text: 'Protein (g)', value: 'protein' },
      { text: 'Actions', value: 'actions', sortable: false }
    ]);
    
    const DEFAULT_RECORD = {
      name: '',
      calories: 0,
      fat: 0.0,
      carbs: 0,
      protein: 0.0,
    };
    
    const record = Vue.ref({ ...DEFAULT_RECORD });
    
    const initialize = () => {
      dialog.value = false;
      $fetch('/api/desserts')
        .then(response => {
          desserts.value = response;
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
      const found = desserts.value.find((dessert) => dessert.id === id);
      if (found) {
        record.value = { ...found };
        dialog.value = true;
      }
    };
    
    const remove = (id) => {
      $swalConfirm(
        '¿Eliminar postre?',
        '¿Estás seguro de que quieres eliminar este postre? Esta acción no se puede deshacer.',
        'warning'
      ).then((result) => {
        if (result.isConfirmed) {
          $fetch(`/api/desserts/${id}`, {
            method: 'DELETE',
          })
          .then(() => {
            initialize();
            $toast.fire({ icon: 'success', title: 'Postre eliminado' });
          })
          .catch(err => {
            $showError('Error', 'No se pudo eliminar el postre');
          });
        }
      });
    };
    
    const save = () => {
      // Limpiar datos antes de enviar (sin incluir id en el body)
      const dessertData = {
        name: record.value.name || '',
        calories: record.value.calories ? parseInt(record.value.calories) : 0,
        fat: record.value.fat ? parseFloat(record.value.fat) : 0.0,
        carbs: record.value.carbs ? parseInt(record.value.carbs) : 0,
        protein: record.value.protein ? parseFloat(record.value.protein) : 0.0,
      };
      
      if (isEditing.value) {
        // Para update: NO incluir el id en el body, solo en la URL
        $fetch(`/api/desserts/${record.value.id}`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(dessertData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Postre actualizado' });
        })
        .catch(err => {
          console.error('Error al actualizar:', err);
          $showError('Error', 'No se pudo actualizar el postre: ' + err);
        });
      } else {
        // Para create: tampoco incluir id
        $fetch('/api/desserts', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(dessertData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Postre agregado' });
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
          
          $showError('Error', 'No se pudo crear el postre: ' + errorMessage);
        });
      }
    };
    
    Vue.onMounted(() => {
      initialize();
    });
    
    return {
      dialog,
      isEditing,
      desserts,
      headers,
      record,
      add,
      edit,
      remove,
      save,
      initialize
    };
  }
});
</script>