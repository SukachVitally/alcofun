import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { Spinner } from "react-bootstrap";
import Product from "../components/Product/Product";
import fetchJson from "../utils/http-utils.js";

function ProductPage() {
  const params = useParams();
  const [product, setProduct] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  async function fetchProduct(params) {
    setIsLoading(true);
    const json = await fetchJson(`product/${params.id}`);

    setProduct(json);
    setIsLoading(false);
  }

  useEffect(() => {
    fetchProduct(params);
  }, []);

  return (
    <div className="Main">
      {isLoading && <Spinner />}
      {!isLoading  && product != null && <Product product={product} />}
    </div>
  );
}

export default ProductPage;
