<template>
    <div class="login-panel">
        <div class="form-signin">
            <h1 class="h3 mb-3 font-weight-normal">sign in</h1>

            <label for="inputEmail" class="sr-only">Email address</label>
            <input type="email" id="inputEmail" class="form-control" placeholder="Email address" v-model="email" required autofocus>
            
            <label for="inputPassword" class="sr-only">Password</label>
            <input type="password" id="inputPassword" class="form-control" placeholder="Password" v-model="password" required>
            
            <button :disabled="isloading" @click="loginAdminUser" class="btn btn-lg btn-primary btn-block" type="button">Sign in</button>
            
            <p class="mt-5 mb-3 text-muted">&copy; 2020</p>
        </div>
    </div>
</template>

<script>
    import firebase from 'firebase'

    export default {
        name: "login",
        data() {
            return {
                isloading: false,
                email: "",
                password: ""
            }
        },

        methods: {
            loginAdminUser(e) {
                e.preventDefault()
                this.isloading=true
                firebase
                    .auth()
                    .signInWithEmailAndPassword(this.email, this.password)
                    .then( response => {
                        alert(`Welcome Administrator ${response.user.email}`)
                        this.$router.go({ path: this.$router.path })
                    },
                    err => alert(err.message))
                    this.isloading=false
            }
        }
    }
</script>


<style scoped>
    .login-panel {
        width: 100%;
        display: flex;
        justify-content: center;
    }

    .form-signin {
        max-width: 800px;
        text-align: center;
        margin-top: 100px;
    }

    .form-signin * {
        margin-bottom: 20px;
    }

    .form-signin h1 {
        text-transform: capitalize;
    }
</style>