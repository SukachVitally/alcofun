import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import { NavLink } from "react-router-dom";
import { useTranslation } from "react-i18next";

const ProviderItem = (props) => {
  const { t } = useTranslation();
  const clickHandler = () => {
    window.open(props.link, "_blank");
  };

  const formattedPrice = props.price.toLocaleString("uk-UA", {
    style: "currency",
    currency: "UAH",
    minimumFractionDigits: 2,
  });

  return (
    <Row className="border-bottom">
      <Col className="align-middle">
        <NavLink to={`/products/?providers=${props.providerId}`}>
          {props.name}
        </NavLink>
      </Col>
      <Col>
        <span>{formattedPrice}</span>
      </Col>
      <Col xs={3}>
        <Button variant="primary" onClick={clickHandler}>
          {t("product.view")}
        </Button>
      </Col>
    </Row>
  );
};

export default ProviderItem;
