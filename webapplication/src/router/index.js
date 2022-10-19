import Vue from 'vue'
import Router from 'vue-router'
import firebase from 'firebase'


import Dashboard from '../components/Dashboard'
import Register from '../components/Register'
import Login from '../components/Login'
import StudentManager from '../components/StudentManager'
import InstructorManager from '../components/InstructorManager'
import ClassroomManager from '../components/ClassroomManager'
import CourseManager from '../components/CourseManager'
import CreateCourse from '../components/CreateCourse'
import ReportManagerAttendance from '../components/ReportManagerAttendance'
import ReportManagerQuiz from '../components/ReportManagerQuiz'
import CreateStudent from '../components/CreateStudent'
import EditStudent from '../components/EditStudent'
import ViewStudent from '../components/ViewStudent'
import CreateInstructor from '../components/CreateInstructor'
import EditInstructor from '../components/EditInstructor'
import ViewInstructor from '../components/ViewInstructor'
import CreateClassroom from '../components/CreateClassroom'
import EditClassroom from '../components/EditClassroom'
import ViewClassroom from '../components/ViewClassroom'
import ViewCourse from '../components/ViewCourse'
import EditCourse from '../components/EditCourse'
import CreateTimeTable from '../components/CreateTimeTable'
import EditTimeTable from '../components/EditTimeTable'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/',
      redirect: '/admin-dashboard',
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard',
      name: 'admin-dashboard',
      component: Dashboard,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/admin-register',
      name: 'register',
      component: Register,
      meta: {
        requiresGuest: true
      }
    },
    {
      path: '/admin-login',
      name: 'login',
      component: Login,
      meta: {
        requiresGuest: true
      }
    },{
      path: '/admin-dashboard/student-manager',
      name: 'manage-students',
      component: StudentManager,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/student-manager/add',
      name: 'create-student',
      component: CreateStudent,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/student-manager/edit/:student_ref',
      name: 'edit-student',
      component: EditStudent,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/student-manager/:student_ref',
      name: 'view-student',
      component: ViewStudent,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/instructor-manager',
      name: 'manage-instructor',
      component: InstructorManager,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/instructor-manager/add',
      name: 'create-instructor',
      component: CreateInstructor,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/instructor-manager/:instructor_ref',
      name: 'view-instructor',
      component: ViewInstructor,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/instructor-manager/edit/:instructor_ref',
      name: 'edit-instructor',
      component: EditInstructor,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/classroom-manager',
      name: 'manage-classroom',
      component: ClassroomManager,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/classroom-manager/add',
      name: 'create-create',
      component: CreateClassroom,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/classroom-manager/:classroom_ref',
      name: 'view-classroom',
      component: ViewClassroom,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/classroom-manager/edit/:classroom_ref',
      name: 'edit-classroom',
      component: EditClassroom,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/course-manager',
      name: 'manage-course',
      component: CourseManager,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/course-manager/add',
      name: 'create-course',
      component: CreateCourse,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/course-manager/:course_ref',
      name: 'view-course',
      component: ViewCourse,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/instructor-manager/edit/:course_ref',
      name: 'edit-course',
      component: EditCourse,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/report-manager/quiz',
      name: 'report-quiz',
      component: ReportManagerQuiz,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/report-manager/attendance',
      name: 'report-attendance',
      component: ReportManagerAttendance,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/timetable/create/:classroom_ref',
      name: 'create-timetable',
      component: CreateTimeTable,
      meta: {
        requiresAuth: true
      }
    },{
      path: '/admin-dashboard/timetable/edit/:classroom_ref',
      name: 'edit-timetable',
      component: EditTimeTable,
      meta: {
        requiresAuth: true
      }
    }
  ]
})


router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!firebase.auth().currentUser) {
            next({
                path: '/admin-login',
                query: {
                    redirect: to.fullPath
                }
            })
        } else {
            next()
        }
    } else if (to.matched.some(record => record.meta.requiresGuest)) {
        if (firebase.auth().currentUser) {
          next({
              path: '/admin-dashboard',
              query: {
                  redirect: to.fullPath
              }
          })
        } else {
            next()
        }
    } else {
        next()
    }
})

// router.replace("admin-dashboard");
// router.replace({ path: '/admin-dashboard', redirect: '/' });

export default router;