<template>
  <div class="login-page">
    <div class="form">

      <form v-if="!successful" class="registration-form" name="form" @submit.prevent="handleRegister">
          <h1 style="text-align: center">Регистрация</h1>
          <div class="form-group">
            <input
              v-model="user.username"
              v-validate="'required|min:3|max:20'"
              type="text"
              class="form-control"
              placeholder="Имя"
              name="логин"
            />
            <div
              v-if="submitted && errors.has('логин')"
              class="alert-error"
            >{{errors.first('логин')}}</div>
          </div>
          <div class="form-group">
            <input
              v-model="user.email"
              v-validate="'required|email|max:50'"
              type="email"
              class="form-control"
              placeholder="Почта"
              name="почта"
            />
            <div
              v-if="submitted && errors.has('почта')"
              class="alert-error"
            >{{errors.first('почта')}}</div>
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
              v-if="submitted && errors.has('пароль')"
              class="alert-error"
            >{{errors.first('пароль')}}</div>
          </div>
          <div class="form-group">
            <input
                v-model="password2"
                v-validate="'required'"
                type="password"
                class="form-control"
                placeholder="Подтвердите пароль"
                name="подтверждения пароля"
            />
            <div
                v-if="submitted && errors.has('подтверждения пароля')"
                class="alert-error"
            >{{errors.first('подтверждения пароля')}}</div>
            <div
                v-if="notSamePasswords"
                class="alert-error"
            >Пароли не совпадают</div>
          </div>
          <button class="btn">
            <span>Зарегистрироваться</span>
          </button>
        <div class="form-group">
          <div
              v-if="message"
              class="alert"
              :class="successful ? 'alert-success' : 'alert-error'">
            {{message}}
          </div>
        </div>
      </form>


<!--      <p v-if="!successful" class="registr">Уже есть аккаунт?-->
<!--        <router-link to="/login">-->
<!--          Войти-->
<!--        </router-link>-->

<!--      </p>-->
<!--      <p v-else class="registr">-->
<!--        <router-link to="/login">-->
<!--          Войти-->
<!--        </router-link>-->

<!--      </p>-->
    </div>

  </div>
</template>

<script>
import User from '../models/user';

export default {
  name: 'Register',
  data() {
    return {
      user: new User('', '', ''),
      password2: '',
      submitted: false,
      successful: false,
      message: ''
    };
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    },
    notSamePasswords () {
      if (this.user.password !== '' && this.password2 !== '') {
        return (this.user.password !== this.password2)
      } else {
        return false
      }
    },
  },
  mounted() {
    if (this.loggedIn) {
      this.$router.push('/profile');
    }
  },
  methods: {
    handleRegister() {
      this.message = '';
      this.submitted = true;
      this.$validator.validate().then(isValid => {
        if (isValid && !this.notSamePasswords) {
          this.$store.dispatch('auth/register', this.user).then(
            data => {
              this.message = data.message;
              this.successful = true;
            },
            error => {
              this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
              this.successful = false;
            }
          );
        }
      });
    }
  }
};
</script>