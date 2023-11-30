import { useState, useEffect } from "react";
import ProductList from "../components/ProductList/ProductList.js";
import Paginator from "../components/Paginator/Paginator.js";
import queryString from "query-string";
import { Spinner } from "react-bootstrap";
import fetchJson from "../utils/http-utils.js";
import "./ProductListPage.css";
import { useLocation } from "react-router-dom";
import filterNullProperties from "../utils/object-utils";

function MainPage() {
  const location = useLocation();

  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [queryParams, setQueryParams] = useState({});

  async function fetchProducts(params) {
    setIsLoading(true);
    const json = await fetchJson(
      "product/?" +
        queryString.stringify({
          ...filterNullProperties(params),
          hot: true,
          sort: "hot,desc",
        })
    );
    setProducts(json.results);
    setPageCount(json.pageCount);
    setCurrentPage(json.currentPage);
    setIsLoading(false);
  }

  useEffect(() => {
    console.log("refresh page");
    const params = queryString.parse(location.search);
    setQueryParams(params);
    fetchProducts(params);
  }, [location]);

  return (
    <div className="Main">
      {isLoading && <Spinner />}
      {!isLoading && <ProductList products={products} />}
      {pageCount > 1 && (
        <Paginator
          path="/"
          currentPage={currentPage}
          pageCount={pageCount}
          params={queryParams}
        />
      )}
    </div>
  );
}

export default MainPage;
