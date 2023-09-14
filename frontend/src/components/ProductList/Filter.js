import Button from "react-bootstrap/Button";
import Offcanvas from "react-bootstrap/Offcanvas";
import FilterBody from "./FilterBody";
import Accordion from "react-bootstrap/Accordion";
import { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import fetchJson from "../../utils/http-utils.js";
import updateList from "../../utils/list-utils.js";
import queryString from "query-string";
import { Spinner } from "react-bootstrap";
import CharacteristicFilter from "./CharacteristicFilter";
import "./Filter.css"

function Filter(props) {
  const location = useLocation();

  const [show, setShow] = useState(false);
  const [categories, setCategories] = useState([]);
  const [isCategoriesLoading, setIsCategoriesLoading] = useState(false);
  const [providers, setProviders] = useState([]);
  const [isProvidersLoading, setIsProvidersLoading] = useState(false);
  const [characteristicsTypes, setCharacteristicsTypes] = useState([]);
  const [isCharacteristicsTypesLoading, setCharacteristicsTypesLoading] =
    useState(false);

  const [enabledCategories, setEnabledCategories] = useState([]);
  const [enabledProviders, setEnabledProviders] = useState([]);
  const [enabledCharacteristics, setEnabledCharacteristics] = useState([]);

  async function fetchCategories() {
    setIsCategoriesLoading(true);
    const json = await fetchJson("category/");
    setCategories(
      json.map((item) => {
        return { id: item.id, name: item.type };
      })
    );

    setIsCategoriesLoading(false);
  }

  async function fetchProviders() {
    setIsProvidersLoading(true);
    const json = await fetchJson("provider/");
    setProviders(
      json.map((item) => {
        return { id: item.id, name: item.type };
      })
    );

    setIsProvidersLoading(false);
  }

  async function fetchCharacteristics() {
    setCharacteristicsTypesLoading(true);
    const json = await fetchJson("characteristic/types");
    setCharacteristicsTypes(json);

    setCharacteristicsTypesLoading(false);
  }

  function updateEnabledItems() {
    const params = queryString.parse(location.search);
    if (params.categories instanceof Array) {
      setEnabledCategories(params.categories.map((id) => parseInt(id)));
    }
    if (params.categories !== undefined) {
      setEnabledCategories([parseInt(params.categories)]);
    }
    if (params.providers instanceof Array) {
      setEnabledProviders(params.providers.map((id) => parseInt(id)));
    }
    if (params.providers !== undefined) {
      setEnabledProviders([parseInt(params.providers)]);
    }
    if (params.characteristics instanceof Array) {
      setEnabledCharacteristics(
        params.characteristics.map((id) => parseInt(id))
      );
    }
    if (params.characteristics !== undefined) {
      setEnabledCharacteristics([parseInt(params.characteristics)]);
    }
  }

  useEffect(() => {
    updateEnabledItems();
    fetchCategories();
    fetchProviders();
    fetchCharacteristics();
  }, []);

  const handleClose = () => {
    console.log("apply filters");
    props.onFilterApply({
      categories: enabledCategories,
      providers: enabledProviders,
      characteristics: enabledCharacteristics,
    });

    setShow(false);
  };

  const handleShow = () => setShow(true);
  const categoryChangeHandler = (id, checked) => {
    setEnabledCategories((ids) => updateList(ids, id, checked));
  };

  const providerChangeHandler = (id, checked) => {
    setEnabledProviders((ids) => updateList(ids, id, checked));
  };

  const characterisitcsChangeHandler = (id, checked) => {
    setEnabledCharacteristics((ids) => updateList(ids, id, checked));
  };

  return (
    <div className="filter">
      <div className="filter__button-block">
        <Button variant="primary" onClick={handleShow}>
          Filters
        </Button>
      </div>

      <Offcanvas show={show} onHide={handleClose}>
        <Offcanvas.Header closeButton>
          <Offcanvas.Title>Filters</Offcanvas.Title>
        </Offcanvas.Header>
        <Offcanvas.Body>
          <Accordion>
            <Accordion.Item eventKey="0">
              <Accordion.Header>Categories</Accordion.Header>
              <Accordion.Body>
                {isCategoriesLoading && <Spinner />}
                {!isCategoriesLoading && (
                  <FilterBody
                    items={categories}
                    type="category"
                    onChange={categoryChangeHandler}
                    enabledItems={enabledCategories}
                  />
                )}
              </Accordion.Body>
            </Accordion.Item>
            <Accordion.Item eventKey="1">
              <Accordion.Header>Providers</Accordion.Header>
              <Accordion.Body>
                {isProvidersLoading && <Spinner />}
                {!isProvidersLoading && (
                  <FilterBody
                    items={providers}
                    type="provider"
                    onChange={providerChangeHandler}
                    enabledItems={enabledProviders}
                  />
                )}
              </Accordion.Body>
            </Accordion.Item>
            {isCharacteristicsTypesLoading && <Spinner />}
            {!isCharacteristicsTypesLoading && (
              <CharacteristicFilter
                types={characteristicsTypes}
                onChange={characterisitcsChangeHandler}
                enabledCharacteristics={enabledCharacteristics}
              />
            )}
          </Accordion>
        </Offcanvas.Body>
      </Offcanvas>
    </div>
  );
}

export default Filter;
