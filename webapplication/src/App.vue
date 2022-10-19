<template>
  <div id="app">
    <page-header></page-header>
    <router-view/>
  </div>
</template>

<script>
  import PageHeader from './components/PageHeader'
export default {
  name: 'App',
  components: {
    "page-header": PageHeader
  }

}
</script>
