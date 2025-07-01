<template id="dessert-iterator-page">
  <app-frame>
    <!-- Header con botón de agregar -->
    <v-container class="pb-0">
      <v-row class="mb-4">
        <v-col>
          <v-btn 
            color="primary" 
            @click="add"
            prepend-icon="mdi-plus"
          >
            Add Dessert
          </v-btn>
        </v-col>
      </v-row>
    </v-container>

    <v-data-iterator :items="desserts" item-value="name">
      <template v-slot:default="{ items, isExpanded, toggleExpand }">
        <v-row>
          <v-col
            v-for="item in items"
            :key="item.raw.name"
            cols="12"
            md="6"
            sm="12"
          >
            <v-card>
              <v-card-title class="d-flex align-center justify-space-between">
                <div class="d-flex align-center">
                  <v-icon
                    :color="item.raw.color"
                    :icon="item.raw.icon"
                    size="18"
                    start
                  ></v-icon>
                  <h4>{{ item.raw.name }}</h4>
                </div>
                
                <!-- Action buttons -->
                <div class="d-flex gap-2">
                  <v-btn
                    icon="mdi-pencil"
                    size="small"
                    color="primary"
                    @click="edit(item.raw.id)"
                  ></v-btn>
                  <v-btn
                    icon="mdi-delete"
                    size="small"
                    color="error"
                    @click="remove(item.raw.id)"
                  ></v-btn>
                </div>
              </v-card-title>

              <v-card-text>
                {{ item.raw.description }}
              </v-card-text>

              <div class="px-4">
                <v-switch
                  :label="`${isExpanded(item) ? 'Hide' : 'Show'} details`"
                  :model-value="isExpanded(item)"
                  density="compact"
                  inset
                  @click="() => toggleExpand(item)"
                ></v-switch>
              </div>

              <v-divider></v-divider>

              <v-expand-transition>
                <div v-if="isExpanded(item)">
                  <v-list :lines="false" density="compact">
                    <v-list-item
                      :title="`🔥 Calories: ${item.raw.calories}`"
                      active
                    ></v-list-item>
                    <v-list-item
                      :title="`🍔 Fat: ${item.raw.fat}g`"
                    ></v-list-item>
                    <v-list-item
                      :title="`🍞 Carbs: ${item.raw.carbs}g`"
                    ></v-list-item>
                    <v-list-item
                      :title="`🍗 Protein: ${item.raw.protein}g`"
                    ></v-list-item>
                    <v-list-item
                      :title="`🧂 Sodium: ${item.raw.sodium}mg`"
                    ></v-list-item>
                    <v-list-item
                      :title="`🦴 Calcium: ${item.raw.calcium}mg`"
                    ></v-list-item>
                    <v-list-item
                      :title="`🧲 Iron: ${item.raw.iron}mg`"
                    ></v-list-item>
                  </v-list>
                </div>
              </v-expand-transition>
            </v-card>
          </v-col>
        </v-row>
      </template>
    </v-data-iterator>

    <!-- Dialog para crear/editar dessert iterator -->
    <v-dialog v-model="dialog" max-width="700px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditing ? 'Edit Dessert' : 'New Dessert' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="record.name"
                  label="Dessert Name"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-textarea
                  v-model="record.description"
                  label="Description"
                  rows="3"
                  required
                ></v-textarea>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.calories"
                  label="Calories"
                  type="number"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.fat"
                  label="Fat (g)"
                  type="number"
                  step="0.1"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.carbs"
                  label="Carbs (g)"
                  type="number"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="record.protein"
                  label="Protein (g)"
                  type="number"
                  step="0.1"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field
                  v-model.number="record.sodium"
                  label="Sodium (mg)"
                  type="number"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field
                  v-model.number="record.calcium"
                  label="Calcium (mg)"
                  type="number"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field
                  v-model.number="record.iron"
                  label="Iron (mg)"
                  type="number"
                  :min="0"
                  required
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog = false">Cancel</v-btn>
          <v-btn color="blue darken-1" text @click="save">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </app-frame>
</template>

<script>
app.component("dessert-iterator-page", {
  template: "#dessert-iterator-page",
  setup() {
    const { getCurrentInstance } = Vue;
    const instance = getCurrentInstance();
    const { $fetch, $toast, $showError, $swalConfirm } = instance.appContext.config.globalProperties;

    // Data
    const desserts = Vue.ref([]);
    const dialog = Vue.ref(false);
    const isEditing = Vue.ref(false);
    
    const DEFAULT_RECORD = {
      name: "",
      description: "",
      calories: null,
      fat: null,
      carbs: null,
      protein: null,
      sodium: null,
      calcium: null,
      iron: null
    };
    
    const record = Vue.ref({ ...DEFAULT_RECORD });

    // Functions
    const initialize = () => {
      dialog.value = false;
      $fetch('/api/dessert-iterator')
        .then(response => {
          desserts.value = response;
          // Add default icon and color for display if not present
          desserts.value.forEach(dessert => {
            if (!dessert.icon) dessert.icon = "mdi-cupcake";
            if (!dessert.color) dessert.color = "#FFADAD";
          });
        })
        .catch(error => {
          $toast.fire({ icon: 'error', title: 'Error loading desserts: ' + error });
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
          $fetch(`/api/dessert-iterator/${id}`, {
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
        description: record.value.description || '',
        calories: record.value.calories ? parseInt(record.value.calories) : 0,
        fat: record.value.fat ? parseFloat(record.value.fat) : 0.0,
        carbs: record.value.carbs ? parseInt(record.value.carbs) : 0,
        protein: record.value.protein ? parseFloat(record.value.protein) : 0.0,
        sodium: record.value.sodium ? parseInt(record.value.sodium) : 0,
        calcium: record.value.calcium ? parseInt(record.value.calcium) : 0,
        iron: record.value.iron ? parseInt(record.value.iron) : 0,
      };
      
      if (isEditing.value) {
        // Para update: NO incluir el id en el body, solo en la URL
        $fetch(`/api/dessert-iterator/${record.value.id}`, {
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
        $fetch('/api/dessert-iterator', {
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
      desserts,
      dialog,
      isEditing,
      record,
      add,
      edit,
      save,
      remove,
      initialize
    };
  },
});
</script>
