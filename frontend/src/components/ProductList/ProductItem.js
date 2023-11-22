import Card from "react-bootstrap/Card";
import { NavLink } from "react-router-dom";
import "./ProductItem.css";
import StarRatings from "react-star-ratings";
import { useTranslation } from "react-i18next";

function ProductItem(props) {
  const { t } = useTranslation();
  const product = props.product;

  const formattedPrice = product.price.toLocaleString("uk-UA", {
    style: "currency",
    currency: "UAH",
    minimumFractionDigits: 2,
  });

  return (
    <Card className="d-inline-block product-item">
      <Card.Img
        variant="top"
        src={product.imageUrl}
        className="product-item__image"
        title={product.name}
        data-toggle="tooltip"
        data-placement="top"
      />
      <Card.Body>
        <Card.Title
          style={{ color: product.hot ? "red" : "black" }}
          title={product.name}
          data-toggle="tooltip"
          data-placement="top"
        >
          {product.name}
        </Card.Title>
        <Card.Subtitle>
          <StarRatings
            rating={product.rating != null ? product.rating.rating : 0}
            starRatedColor="#ebd834"
            numberOfStars={5}
            starDimension="25"
            name="rating"
          />
        </Card.Subtitle>
        <Card.Body>
          {t('products.best_price_is')} <b>{formattedPrice}</b>
        </Card.Body>
        <div className="product-item__footer">
          <NavLink to={`/products/${product.id}`} className="btn btn-primary">
            {t('products.view')}
          </NavLink>
        </div>
      </Card.Body>
    </Card>
  );
}

export default ProductItem;
