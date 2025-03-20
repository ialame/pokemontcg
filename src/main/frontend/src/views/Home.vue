<template>
  <div>
    <h1>Explorer les cartes Pokémon TCG</h1>
    <div v-if="loading" class="spinner">
      <span class="spinner-icon"></span>
      Chargement...
    </div>
    <div v-else>
      <label for="series">Choisir une série :</label>
      <select id="series" v-model="selectedSeries" @change="fetchSets">
        <option value="">-- Sélectionner une série --</option>
        <option v-for="serie in series" :key="serie.id" :value="serie.name">{{ serie.name }}</option>
      </select>
    </div>
    <!-- Reste du template inchangé -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { Card, Set, Serie } from '@/types/card';

const series = ref<Serie[]>([]);
const sets = ref<Set[]>([]);
const cards = ref<Card[]>([]);
const selectedSeries = ref<string>('');
const selectedSet = ref<string>('');
const loading = ref(false);

const fetchSeries = async () => {
  loading.value = true;
  try {
    const response = await axios.get<Serie[]>('http://localhost:8081/api/series');
    console.log('Réponse de /api/series :', response.data);
    if (Array.isArray(response.data)) {
      series.value = response.data;
    } else {
      console.error('Les données ne sont pas un tableau :', response.data);
      series.value = [];
    }
  } catch (error) {
    console.error('Erreur lors de la récupération des séries :', error);
    series.value = [];
  } finally {
    loading.value = false;
  }
};

// ... reste du script inchangé
onMounted(fetchSeries);
</script>