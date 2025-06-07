<template id="app-frame">
  <v-app id="inspire">
    <v-navigation-drawer v-model="drawer" width="300">
      <v-sheet class="pa-4" color="grey-lighten-4">
        <v-row class="d-flex align-center justify-center">
          <v-col class="text-center" cols="auto">
            <v-avatar :image="userImg" class="mb-8" color="grey-darken-1" size="64" />
          </v-col>
          <v-col class="text-center" cols="auto">
            <v-list-item-content>
              <v-list-item-title class="text-h6">Fernando Leon</v-list-item-title>
              <v-list-item-subtitle class="text-lowercase">9 IDGS G-2</v-list-item-subtitle>
            </v-list-item-content>
          </v-col>
        <v-list>
          <v-list-subheader>PAGES</v-list-subheader>

          <v-list-item
            v-for="(item, i) in items"
            :key="i"
            :value="item"
            color="primary"
            rounded="xl"
          >
            <template v-slot:prepend>
              <v-icon :icon="item.icon"></v-icon>
            </template>

            <v-list-item-title v-text="item.text"></v-list-item-title>
          </v-list-item>
        </v-list>
        </v-row>
      </v-sheet>

      <v-divider></v-divider>
    </v-navigation-drawer>

    <v-app-bar>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>Application</v-app-bar-title>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <slot></slot>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
  app.component("app-frame", {
    template: "#app-frame",
    data: () => ({ 
      drawer: null,
      userImg: 'images/profile-vue.png',
      items: [
      { text: 'Home Page', icon: 'mdi-clock' },
      { text: 'Popular books', icon: 'mdi-account' },
  ] })
  })
</script>