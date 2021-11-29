<template>
  <div class="user-menu">
    <router-link to="/profile"><span class="material-icons">person_outline</span>
      Мой профиль
    </router-link>
    <router-link to="/absences"><span class="material-icons">schedule</span>
      Пропуски работы
    </router-link>
    <router-link to="/salary"><span class="material-icons">attach_money</span>
      Зарплата
    </router-link>
    <router-link v-if="isUserAccountant" to="/users"><span class="material-icons">people_outline</span>
      Список пользователей
    </router-link>
  </div>
</template>

<script>

export default {
  name: "Menu",
  data() {
    return {
      message: ''
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    isUserAccountant() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_ACCOUNTANT');
      }

      return false;
    }
  }
}
</script>