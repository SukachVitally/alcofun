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
import "./Filter.css";
import PriceFilter from "./PriceFilter";
import Sorting from "./Sorting";
import HotPriceFilter from "./HotPriceFilter";

// Todo: simplify it!
function Filter(props) {
  const location = useLocation();

  const [show, setShow] = useState(false);
  const [categories, setCategories] = useState([]);
  const [isCategoriesLoading, setIsCategoriesLoading] = useState(false);
  const [providers, setProviders] = useState([]);
  const [isProvidersLoading, setIsProvidersLoading] = useState(false);
  const [characteristicsTypes, setCharacteristicsTypes] = useState([]);
  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);
  const [priceRange, setPriceRange] = useState({min: null, max: null})
  const [isPriceRageLoading, setPriceRageLoading] = useState(false);
  const [hot, setHot] = useState(false);
  const [sortingField, setSortingField] = useState(null);
  const [sortingOrder, setSortingOrder] = useState("desc");
  const [isCharacteristicsTypesLoading, setCharacteristicsTypesLoading] = useState(false);

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

  async function fetchPriceRange() {
    setPriceRageLoading(true);
    const json = await fetchJson("product/price-range");
    setPriceRange(json);

    setPriceRageLoading(false);
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

    if (params.price_min !== undefined && params.price_min != null) {
      setMinPrice([parseInt(params.price_min)]);
    }

    if (params.price_max !== undefined && params.price_max != null) {
      setMaxPrice([parseInt(params.price_max)]);
    }

    if (params.sort !== undefined && params.sort != null) {
      const pair = params.sort.split(",");
      setSortingField(pair[0]);
      setSortingOrder(pair[1]);
    }

    if (params.hot !== undefined) {
      setHot(params.hot == "true");
    }
  }

  useEffect(() => {
    updateEnabledItems();
    fetchCategories();
    fetchProviders();
    fetchCharacteristics();
    fetchPriceRange();
  }, []);

  const handleClose = () => {
    console.log("apply filters");
    props.onFilterApply({
      categories: enabledCategories,
      providers: enabledProviders,
      characteristics: enabledCharacteristics,
      price_min: minPrice,
      price_max: maxPrice,
      sort_field: sortingField,
      sort_order: sortingOrder,
      hot: hot,
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

  const priceRangeHandler = (range) => {
    setMinPrice(range.min);
    setMaxPrice(range.max);
  };

  const sortingHandler = (field, order) => {
    setSortingField(field);
    setSortingOrder(order);
  };

  const hotHandler = (value) => {
    setHot(value);
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
          <HotPriceFilter hot={hot} onChange={hotHandler} />
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
            <Accordion.Item eventKey="100">
              <Accordion.Header>Price</Accordion.Header>
              <Accordion.Body>
                {!isPriceRageLoading && (
                  <PriceFilter
                    onChange={priceRangeHandler}
                    priceRange={priceRange}
                    price={minPrice}
                    maxPrice={maxPrice}
                  />
                )}
              </Accordion.Body>
            </Accordion.Item>
          </Accordion>
          <Sorting
            onChange={sortingHandler}
            field={sortingField}
            order={sortingOrder}
          />
        </Offcanvas.Body>
      </Offcanvas>
    </div>
  );
}

export default Filter;
