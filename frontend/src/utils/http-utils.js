const BASE_URL = "http://localhost:7080/api/";


async function fetchJson(path) {
  const response = await fetch(BASE_URL + path);
  return await response.json();
}

export default fetchJson;
