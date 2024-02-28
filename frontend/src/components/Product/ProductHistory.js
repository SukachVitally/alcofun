import { useTranslation } from "react-i18next";

import Graph from "./Graph";
import Container from "react-bootstrap/Container";
import { useState, useEffect } from "react";
import { Spinner } from "react-bootstrap";
import fetchJson from "../../utils/http-utils.js";

const ProductHistory = (props) => {
  const { t } = useTranslation();

  const [isLoading, setIsLoading] = useState(false);
  const [prices, setPrices] = useState(null);

  async function fetchPrices(id) {
    setIsLoading(true);
    const json = await fetchJson(`price/history/${id}`);

    setPrices(json);
    setIsLoading(false);
  }

  useEffect(() => {
    console.log("here", props.productId)
    fetchPrices(props.productId);
  }, [props.productId]);

  return (
    <>
      <h3>{t('product.price_dynamics')}</h3>
      <Container>
        {isLoading && <Spinner />}
        {!isLoading && prices != null && <Graph prices={prices} />}
      </Container>
    </>
  );
}

export default ProductHistory;