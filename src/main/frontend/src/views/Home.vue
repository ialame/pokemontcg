<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { Serie, Set, Card } from '@/types/card';

const series = ref<Serie[]>([]);
const selectedSeries = ref<string>('');
const sets = ref<Set[]>([]);
const selectedSet = ref<string>('');
const cards = ref<Card[]>([]);
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

const fetchSets = async (seriesName: string) => {
  if (!seriesName) {
    sets.value = [];
    cards.value = [];
    return;
  }
  loading.value = true;
  try {
    const response = await axios.get<Set[]>(`http://localhost:8081/api/series/${seriesName}/sets`);
    console.log('Réponse brute de /api/series/{seriesName}/sets :', response.data);
    console.log('Est-ce un tableau ?', Array.isArray(response.data));
    if (Array.isArray(response.data)) {
      sets.value = response.data;
    } else {
      console.error('Les données ne sont pas un tableau :', response.data);
      sets.value = [];
    }
  } catch (error) {
    console.error('Erreur lors de la récupération des sets :', error);
    sets.value = [];
  } finally {
    loading.value = false;
  }
};

const fetchCards = async (setId: string) => {
  if (!setId) {
    cards.value = [];
    return;
  }
  loading.value = true;
  try {
    const response = await axios.get<Card[]>(`http://localhost:8081/api/sets/${setId}/cards`);
    console.log('Réponse brute de /api/sets/{setId}/cards :', response.data);
    console.log('Est-ce un tableau ?', Array.isArray(response.data));
    if (Array.isArray(response.data)) {
      cards.value = response.data;
    } else {
      console.error('Les données ne sont pas un tableau :', response.data);
      cards.value = [];
    }
  } catch (error) {
    console.error('Erreur lors de la récupération des cartes :', error);
    cards.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(fetchSeries);

watch(selectedSeries, (newSeries) => {
  console.log('Série sélectionnée :', newSeries);
  fetchSets(newSeries);
  selectedSet.value = '';
  cards.value = [];
});

watch(selectedSet, (newSet) => {
  console.log('Set sélectionné :', newSet);
  fetchCards(newSet);
});
</script>

<template>
  <div class="container">
    <div class="select-container">
      <label for="series">Choisir une série :</label>
      <select id="series" v-model="selectedSeries">
        <option value="">-- Sélectionner une série --</option>
        <option v-for="serie in series" :key="serie.id" :value="serie.name">{{ serie.name }}</option>
      </select>
    </div>

    <div v-if="loading" class="loading">
      Chargement...
    </div>

    <div v-else-if="sets.length > 0" class="select-container">
      <label for="sets">Choisir un set :</label>
      <select id="sets" v-model="selectedSet">
        <option value="">-- Sélectionner un set --</option>
        <option v-for="set in sets" :key="set.id" :value="set.id">{{ set.name }}</option>
      </select>
    </div>
    <div v-else-if="selectedSeries" class="no-data">
      Aucun set disponible pour cette série.
    </div>

    <div v-if="cards.length > 0" class="cards-section">
      <h3>Cartes :</h3>
      <div class="card-grid">
        <div v-for="card in cards" :key="card.id" class="card-item">
          <img v-if="card.imagePath" :src="card.imagePath" :alt="card.name" class="card-image" />
          <p>{{ card.name }}</p>
        </div>
      </div>
    </div>
    <div v-else-if="selectedSet" class="no-data">
      Aucune carte disponible pour ce set.
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.select-container {
  margin-bottom: 20px;
}

.select-container label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.select-container select {
  width: 100%;
  max-width: 300px;
  padding: 8px;
  font-size: 16px;
}

.loading {
  text-align: center;
  font-size: 18px;
  margin: 20px 0;
}

.no-data {
  text-align: center;
  color: #666;
  margin: 20px 0;
}

.cards-section {
  margin-top: 20px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.card-item {
  text-align: center;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-image {
  max-width: 100%;
  height: auto;
  border-radius: 5px;
}

.card-item p {
  margin: 10px 0 0;
  font-size: 14px;
}
</style>