<template>
  <div v-if="currentUser">
    <v-header
        v-bind:user="{
        firstName: user.firstName,
        profilePicture: user.profilePicture,
        gender: user.gender
      }">
    </v-header>

    <v-userEdit v-if="isEditorActive"
                   :userToEdit = "userToEdit"
                   :success = "successEdit"
                   :close="closeEditor"></v-userEdit>

    <div class="container">
      <v-menu></v-menu>

      <div class="content" style="width: calc(80% - 15% - 285px + 245px)">

        <h1 class="h1">Список пользователей</h1>
        <div class="separator"></div>
        <div class="row" style="align-items: flex-start;">
          <div class="column">
            <div class="row user-info">Должность:</div>
            <div class="row user-info">Имя пользователя:</div>
          </div>
          <div class="column" style="width: 300px">
            <div class="row"  v-click-outside="deactivatePositionFocus" style="margin: 8px 0; position: relative">
              <div style="position: relative; width: 100%">
                <input v-on:focus="positionFocus = true" type="text" class="border-input" style="width: 100%" v-model="selectedPosition">
                <span class="material-icons close-icon" @click="selectedPosition=''; selectedPositionId='';" style="position: absolute; top:calc(50% - 10px); right:18px;">close</span>
              </div>

              <div v-if="positionFocus && positions.filter(i => i.name.toLowerCase().startsWith(selectedPosition.toLowerCase())).length" class="input-select">
                <div v-for="position in positions.filter(i => i.name.toLowerCase().startsWith(selectedPosition.toLowerCase()))" :key="position.id" @click="selectedPosition=position.name; selectedPositionId=position.id; positionFocus=false; getAbsences()">{{position.name}}</div>
              </div>
            </div>
            <div class="row" style="margin: 8px 0;">
              <div style="position: relative; width: 100%">
                <input type="text" class="border-input" style="width: 100%" v-model="selectedWorker">
                <span class="material-icons close-icon" @click="selectedWorker=''" style="position: absolute; top:calc(50% - 10px); right:18px;">close</span>
              </div>
            </div>
          </div>
          <div class="row" style="margin: 10px 15px 10px auto"><div class="btn btn-thin" style="margin: 0" @click="openEditor({})">Добавить пользователя</div></div>
        </div>
        <div class="separator"></div>
        <div class="row">
          <div v-if="users.length" style="width: 100%;">

            <div class="userAbsences" @click="openEditor(user)"
                 v-bind:style="success === user.id ? 'box-shadow: 0 2px 0 0 #a3dba8, 0 0 0 2px #b3eec1;': 'box-shadow: 0 1px 0 0 #dce1e6, 0 0 0 1px #e7e8ec;'"
                 :ref="'user'+user.id"
                 v-for="user in filteredUsers" :key="user.id">

              <img :src="user.profilePicture ?
                             require('../../public/images/store/'+user.profilePicture) :
                             require('../../public/images/default'+(user.gender ? 'Woman':'Man')+'.png')"
                   v-bind:alt="user.firstName"/>

              <div class="column" style="justify-content: space-between">
                <div style="margin-bottom: 12px; width: max-content">{{user.lastName}} {{user.firstName}} {{user.patronymic}}</div>
                <div style="width: max-content">{{user.workerInfo ? user.workerInfo.position.name : 'Бухгалтер'}}</div>
              </div>

              <div style="margin-left: auto;">
                <span v-on:click.stop="deleteUser(user.id)" class="material-icons close-icon">close</span>
              </div>
            </div>

          </div>
          <div style="padding-left: 15px" v-else>По выбранным критериям пользователей не найдено</div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import Menu from "@/views/components/Menu";
import Header from "@/views/components/Header";
import UserService from "@/services/user.service";
import userEditor from "@/views/components/userEditor";


export default {
  name: "Users",
  data(){
    return{
      message: '',

      users: [],

      selectedWorker: '',

      selectedPosition: '',
      selectedPositionId: '',
      positionFocus: false,

      workers:[],
      positions:[],

      isEditorActive: false,
      userToEdit:{},
      success: 0,

      user: {
        firstName: '',
        profilePicture: '',
        gender: false
      }
    };
  },
  components:{
    'v-menu' : Menu,
    'v-header': Header,
    'v-userEdit': userEditor
  },
  computed: {
    filteredUsers(){
      return this.users
          .filter(
              i => i.workerInfo.position.name.startsWith(this.selectedPosition)
              && (
                      i.firstName.startsWith(this.selectedWorker)
                      || i.lastName.startsWith(this.selectedWorker)
                      || i.patronymic.startsWith(this.selectedWorker)
                      || i.email.startsWith(this.selectedWorker)

                  )
          )
    },

    currentUser() {
      return this.$store.state.auth.user;
    },
    isUserAdmin() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_ADMIN');
      }

      return false;
    },
    isUserAccountant() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_ACCOUNTANT');
      }

      return false;
    }
  },
  methods: {
    openEditor(user){
      this.userToEdit = user.id;
      this.isEditorActive = true;
    },
    closeEditor(){
      this.isEditorActive = false;
    },
    async scrollToUser(number_slide){
      let slide = this.$refs['user'+number_slide][0]

      let count = 100;
      let top = window.scrollY + slide.getBoundingClientRect().y;
      let start_top = window.scrollY;

      let delta = (top - start_top) / count;
      for (let i=0;i<count; i++) {
        await new Promise(resolve => {
          window.setTimeout(function () {
            resolve()
          }, 1000 / count)
        })
        window.scrollTo(0, start_top + i*delta)
      }
    },
    successEdit(user){
      this.closeEditor();
      let index = this.users.findIndex(i => i.id === user.id);
      if (index === -1 && user.workerInfo) this.users.push(user);//if you want show SUCCESS message after add ACCOUNTANT, do it here
      else this.users[index] = user;


      this.success = user.id;
      setTimeout(()=>this.success=0,1500)
    },



    deactivatePositionFocus(){
      this.selectedPosition = this.selectedPosition && this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())) ? this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())).name : ''
      this.selectedPositionId = this.selectedPosition && this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())) ? this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())).id : ''
      this.positionFocus = false;
    },

    changeUser(){
      UserService.changeUser(this.currentUser.id, this.user).then(
          response => {
            response.data;
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    deleteUser(userId){
      UserService.deleteUser(userId).then(
          response => {
            response.data;
            let index = this.users.findIndex(i => i.id === userId);
            this.users.splice(index, 1);
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      )
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
    UserService.getUser(this.currentUser.id).then(
        response => {
          this.user = response.data;
          this.user.birthday = new Date(response.data.birthday);
        },
        error => {
          this.message =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
    UserService.getAllUsers().then(
        response => {
          this.users = response.data.filter(i => i.workerInfo);
        },
        error => {
          this.message =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
    UserService.getPositions().then(
        response => {
          this.positions = response.data;
        },
        error => {
          this.message =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
  },
  updated(){
    if (this.success){
      this.scrollToUser(this.success);
    }
  }
}
</script>