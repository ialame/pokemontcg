// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import CardDetail from '../views/CardDetail.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/card/:id',
    name: 'CardDetail',
    component: CardDetail,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;