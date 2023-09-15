async function fetchJson(path) {
  const response = await fetch(process.env.REACT_APP_SERVER_API_URL + path);
  return await response.json();
}

export default fetchJson;
