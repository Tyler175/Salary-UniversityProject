<template>
  <div class="header">
    <div class="header-container">
      <router-link to="/"><img class="brand-name" src="../../../public/images/logo.png" alt="logo"/></router-link>
      <div class="profile" v-click-outside="deactivate" v-if="user" v-on:click="isActive = !isActive">
        {{user.firstName}}
        <img :src="user.profilePicture ?
             require('../../../public/images/store/'+user.profilePicture) :
             require('../../../public/images/default'+(user.gender ? 'Woman':'Man')+'.png')"
             v-bind:alt="user.firstName"/>

        <span class="material-icons" v-bind:style="{ 'padding-right': '0px', transform: 'rotate('+ (isActive ? '90':'0')+'deg)'}">chevron_right</span>
        <div class="profile-menu" v-bind:style="[isActive ? {display:'block'} : {display: 'none'}]">
          <router-link to="/profile"><div>Профиль</div></router-link>
          <div class="separator"></div>
          <div @click.prevent="logOut">Выйти</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: "Header",
  props: ['user'],
  data() {
    return {
      isActive: false
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  methods:{
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    },
    deactivate(){
      this.isActive = false;
    }
  }
}
</script>