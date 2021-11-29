<style scoped>
  .absence-editor{
    width: 300px;

    vertical-align: middle;
    text-align: center;
  }
</style>
<template>
  <div>
    <div class="cover" @click="close()"></div>
    <div class="absence-editor">
      <div>{{message}}
        <span class="material-icons close-icon" @click="close()">close</span></div>

    </div>
  </div>
</template>

<script>
export default {
  name: "message",
  props: ['message', 'close'],
}
</script>

