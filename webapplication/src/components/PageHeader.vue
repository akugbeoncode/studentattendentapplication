<template>
    <div class="page-header">
        <nav class="navbar navbar-dark bg-dark">
            <div class="container-fluid">
                <router-link class="navbar-brand" to="/admin-dashboard">STA Administrator</router-link>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav mr-auto">
                    
                    </ul>
                </div>
                 <form class="form-inline my-2 my-lg-0">
                    <button v-if="isLoggedIn" @click="logoutAdminUser" class="btn btn-sm btn-secondary my-2 my-sm-0" type="submit">Logout</button>
                </form>
            </div>
        </nav>
    </div>
</template>

<script>

    import firebase from 'firebase'
    
    export default {
        name: 'page-header',
        components: {
            
        },
        data () {
            return {
                isLoggedIn: false,
                currentUser: ''
            }
        },

        methods: {
            logoutAdminUser() {
                firebase
                    .auth()
                    .signOut()
                    .then(() => {
                        this.$router.go({ path: this.$router.path })
                    })
            }
        },

        created() {
            if (firebase.auth().currentUser) {
                this.isLoggedIn = true
                this.currentUser = firebase.auth().currentUser.email
            }
        }
    }
</script>

<style scoped>
    
</style>