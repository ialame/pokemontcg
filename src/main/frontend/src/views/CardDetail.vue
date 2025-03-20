<template>
  <div>
    <h1>Détails de la carte {{ cardId }}</h1>
    <p v-if="loading">Chargement...</p>
    <div v-else-if="card && card.images">
      <img :src="card.images.large" alt="Card Image" />
      <p>Nom : {{ card.name }}</p>
      <p>ID : {{ card.id }}</p>
    </div>
    <p v-else-if="error">{{ error }}</p>
    <p v-else>Carte non trouvée ou données invalides</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { Card } from '@/types/card';

const route = useRoute();
const cardId = route.params.id as string;
const card = ref<Card | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const fetchCard = async () => {
  try {
    console.log('Requête pour :', `http://localhost:8081/api/cards/${cardId}`);
    const response = await axios.get<Card>(`http://localhost:8081/api/cards/${cardId}`);
    console.log('Données reçues :', response.data);
    card.value = response.data;
  } catch (err: any) {
    error.value = 'Erreur : ' + (err.response?.data || err.message);
    console.error('Erreur Axios :', err);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchCard);
</script>