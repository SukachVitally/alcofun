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
    <NavLink to={`/products/${product.id}`}>
      <Card className="d-inline-block product-item">
        {product.hot && <span className="hot-percent">-{product.percent}%</span>}
        <div className="product-item__image">
          <Card.Img
            variant="top"
            src={product.imageUrl}
            title={product.name}
            data-toggle="tooltip"
            data-placement="top"
          />
        </div>
        <Card.Body>
          <Card.Title
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
        </Card.Body>
      </Card>
    </NavLink>
  );
}

export default ProductItem;
