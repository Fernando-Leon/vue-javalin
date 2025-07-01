<template id="games-page">
<app-frame>
  <v-card>
    <v-data-iterator
      :items="games"
      :items-per-page="3"
      :search="search"
    >
      <template v-slot:header>
        <v-toolbar class="px-2">
          <v-text-field
            v-model="search"
            density="comfortable"
            placeholder="Search"
            prepend-inner-icon="mdi-magnify"
            style="max-width: 300px;"
            variant="solo"
            clearable
            hide-details
          ></v-text-field>
          
          <v-spacer></v-spacer>
          
          <v-btn
            color="primary"
            prepend-icon="mdi-plus"
            @click="add"
          >
            Add Game
          </v-btn>
        </v-toolbar>
      </template>

      <template v-slot:default="{ items }">
        <v-container class="pa-2" fluid>
          <v-row dense>
            <v-col
              v-for="item in items"
              :key="item.title"
              cols="auto"
              md="4"
            >
              <v-card class="pb-3" border flat>
                <v-img :src="item.raw.img"></v-img>

                <v-list-item :subtitle="item.raw.description" class="mb-2">
                  <template v-slot:title>
                    <strong class="text-h6 mb-2">{{ item.raw.name }}</strong>
                  </template>
                </v-list-item>

                <div class="d-flex justify-space-between px-4">
                  <div class="d-flex align-center text-caption text-medium-emphasis me-1">
                    <v-icon icon="mdi-clock" start></v-icon>
                    <div class="text-truncate">{{ item.raw.minutes }} min</div>
                  </div>

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
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </template>

      <template v-slot:footer="{ page, pageCount, prevPage, nextPage }">
        <div class="d-flex align-center justify-center pa-4">
          <v-btn
            :disabled="page === 1"
            density="comfortable"
            icon="mdi-arrow-left"
            variant="tonal"
            rounded
            @click="prevPage"
          ></v-btn>

          <div class="mx-2 text-caption">
            Page {{ page }} of {{ pageCount }}
          </div>

          <v-btn
            :disabled="page >= pageCount"
            density="comfortable"
            icon="mdi-arrow-right"
            variant="tonal"
            rounded
            @click="nextPage"
          ></v-btn>
        </div>
      </template>
    </v-data-iterator>
  </v-card>

  <!-- Dialog para crear/editar juego -->
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-title>
        <span class="text-h5">{{ isEditing ? 'Edit Game' : 'New Game' }}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="record.name"
                label="Game Name"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="record.description"
                label="Description"
                rows="4"
                required
              ></v-textarea>
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model.number="record.minutes"
                label="Duration (minutes)"
                type="number"
                :min="1"
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
const { shallowRef } = Vue

app.component("games-page", {
 template: "#games-page",
  setup() {
    const { getCurrentInstance } = Vue;
    const instance = getCurrentInstance();
    const { $fetch, $toast, $showError, $swalConfirm } = instance.appContext.config.globalProperties;
    
    const search = shallowRef('');
    const dialog = Vue.ref(false);
    const isEditing = Vue.ref(false);
    const games = Vue.ref([]);
    
    const DEFAULT_RECORD = {
      name: '',
      description: '',
      minutes: 1,
    };
    
    const record = Vue.ref({ ...DEFAULT_RECORD });
    
    const initialize = () => {
      dialog.value = false;
      $fetch('/api/games')
        .then(response => {
          // Add default image for all games
          games.value = response.map(game => ({
            ...game,
            img: 'https://cdn.vuetifyjs.com/docs/images/graphics/games/4.png' // Same image for all
          }));
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
      const found = games.value.find((game) => game.id === id);
      if (found) {
        record.value = { ...found };
        dialog.value = true;
      }
    };
    
    const remove = (id) => {
      $swalConfirm(
        '¿Eliminar juego?',
        '¿Estás seguro de que quieres eliminar este juego? Esta acción no se puede deshacer.',
        'warning'
      ).then((result) => {
        if (result.isConfirmed) {
          $fetch(`/api/games/${id}`, {
            method: 'DELETE',
          })
          .then(() => {
            initialize();
            $toast.fire({ icon: 'success', title: 'Juego eliminado' });
          })
          .catch(err => {
            $showError('Error', 'No se pudo eliminar el juego');
          });
        }
      });
    };
    
    const save = () => {
      const gameData = {
        name: record.value.name || '',
        description: record.value.description || '',
        minutes: record.value.minutes ? parseInt(record.value.minutes) : 1,
      };
      
      if (isEditing.value) {
        $fetch(`/api/games/${record.value.id}`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(gameData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Juego actualizado' });
        })
        .catch(err => {
          console.error('Error al actualizar:', err);
          $showError('Error', 'No se pudo actualizar el juego: ' + err);
        });
      } else {
        $fetch('/api/games', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(gameData)
        })
        .then(() => {
          dialog.value = false;
          initialize();
          $toast.fire({ icon: 'success', title: 'Juego agregado' });
        })
        .catch(err => {
          console.error('Error al crear:', err);
          let errorMessage = err.message;
          
          try {
            if (err.message && err.message.includes('REQUEST_BODY')) {
              const errorData = JSON.parse(err.message.split('Error: ')[1]);
              if (errorData.REQUEST_BODY && errorData.REQUEST_BODY.length > 0) {
                errorMessage = errorData.REQUEST_BODY[0].message;
              }
            }
          } catch (parseError) {
            console.warn('Could not parse error message:', parseError);
          }
          
          $showError('Error', 'No se pudo crear el juego: ' + errorMessage);
        });
      }
    };
    
    Vue.onMounted(() => {
      initialize();
    });

    return {
      search,
      games,
      dialog,
      isEditing,
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
