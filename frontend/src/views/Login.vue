<template>
  <div>
    <v-header></v-header>
    <form class="login-form" name="form" @submit.prevent="handleLogin">
      <div class="form-group">
        <h1 style="text-align: center">Вход</h1>
      </div>
      <div class="form-group">
        <input
            v-model="user.email"
            v-validate="'required|email|max:50'"
            type="email"
            class="form-control"
            placeholder="Логин"
            name="логин"
        />
        <div
            v-if="errors.has('логин')"
            class="alert-error"
        >{{errors.first('логин')}}</div>
      </div>
      <div class="form-group">

        <input
            v-model="user.password"
            v-validate="'required|min:6|max:40'"
            type="password"
            class="form-control"
            placeholder="Пароль"
            name="пароль"
        />
        <div
            v-if="errors.has('пароль')"
            class="alert-error"
        >{{errors.first('пароль')}}</div>
      </div>
      <button class="btn" :disabled="loading">
        <span v-show="loading" class="spinner-border spinner-border-sm"></span><!--Not used yet-->
        <span>Войти</span>
      </button>
      <div v-if="message" class="form-group">
        <div class="alert alert-error" role="alert">
          <div v-if="message === 'Bad credentials'">Неверный логин или пароль</div>
          <div v-else>{{message}}</div>
        </div>
      </div>
  <!--        <p class="registr">Нет аккаунта?-->
  <!--          <router-link to="/register">-->
  <!--          Зарегистрироваться-->
  <!--          </router-link>-->

  <!--        </p>-->
    </form>
  </div>
</template>

<script>
import User from '../models/user';
import Header from "@/views/components/Header";

export default {
  name: 'Login',
  data() {
    return {
      user: new User('', ''),
      loading: false,
      message: ''
    };
  },
  components:{
    'v-header': Header
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    }
  },
  created() {
    if (this.loggedIn) {
      this.$router.push('/profile');
    }
  },
  methods: {
    handleLogin() {
      this.loading = true;
      this.$validator.validateAll().then(isValid => {
        if (!isValid) {
          this.loading = false;
          return;
        }

        if (this.user.email && this.user.password) {
          this.$store.dispatch('auth/login', this.user).then(
            () => {
              this.$router.push('/profile');
            },
            error => {
              this.loading = false;
              this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
            }
          );
        }
      });
    }
  }
};
</script>

