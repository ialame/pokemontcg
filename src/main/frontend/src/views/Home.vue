<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { Serie } from '@/types/card';

const series = ref<Serie[]>([]);
const selectedSeries = ref<string>('');
const loading = ref(false);

const fetchSeries = async () => {
  loading.value = true;
  try {
    const response = await axios.get<Serie[]>('http://localhost:8081/api/series');
    console.log('Réponse brute de /api/series :', response.data);
    console.log('Est-ce un tableau ?', Array.isArray(response.data));
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

onMounted(fetchSeries);
</script>

<template>
  <div>
    <label for="series">Choisir une série :</label>
    <select id="series" v-model="selectedSeries">
      <option value="">-- Sélectionner une série --</option>
      <option v-for="serie in series" :key="serie.id" :value="serie.name">{{ serie.name }}</option>
    </select>
  </div>
</template>