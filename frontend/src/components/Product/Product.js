import CharacteristicList from "./CharacteristicList";
import ProviderList from "./ProviderList";
import ProductHistory from "./ProductHistory";
import "./Product.css";
import { useState } from "react";
import StarRatings from "react-star-ratings";
import { useTranslation } from "react-i18next";
import { NavLink } from "react-router-dom";
import queryString from "query-string";
import Image from "react-bootstrap/Image";
import filterNullProperties from "../../utils/object-utils";

const Product = (props) => {
  const { t } = useTranslation();
  const product = props.product;
  const brandList = product.characteristics.filter(
    (characteristic) => characteristic.name == "Бренд" // Use characteristic id!!!
  );
  const brand = brandList.length > 0 ? brandList[0] : null;
  const [fullDescription, setFullDescription] = useState(false);

  const descriptionClickHandler = () => {
    setFullDescription(true);
  };

  return (
    <div className="product-layout">
      <nav aria-label="breadcrumb">
        <ol className="breadcrumb">
          <li className="breadcrumb-item">
            <NavLink className="navbar-brand" to="/products">
              {t("menu.products")}
            </NavLink>
          </li>
          {brand && (
            <li className="breadcrumb-item">
              <NavLink
                className="navbar-brand"
                to={`/products?${queryString.stringify({
                  characteristics: brand.id,
                })}`}
              >
                {brand.value}
              </NavLink>
            </li>
          )}
          {brand && (
            <li className="breadcrumb-item active" aria-current="page">
              <NavLink
                className="navbar-brand"
                to={`/products?${queryString.stringify(
                  filterNullProperties({
                    categories: product.categoryId,
                    characteristics: brand.id,
                  })
                )}`}
              >
                {product.categoryName}
              </NavLink>
            </li>
          )}
        </ol>
      </nav>
      <div className="product-view">
        {product.hot && <span className="hot-percent">-{product.percent}%</span>}
        <h2>{product.name}</h2>
        <StarRatings
          rating={product.rating != null ? product.rating.rating : 0}
          starRatedColor="#ebd834"
          numberOfStars={5}
          starDimension="25"
          name="rating"
        />
        <p className="text-end">Barcode: {product.ean}</p>

        <Image src={product.imageUrl} thumbnail />

        <h3>{t("product.description")}</h3>
        <div
          className={`product-view__description_${fullDescription ? "full" : "limited"
            }`}
          onClick={descriptionClickHandler}
        >
          {product.description}
        </div>

        <CharacteristicList characteristics={product.characteristics} />
        {product.providers && <ProviderList providers={product.providers} />}
        <ProductHistory />
      </div>

    </div>
  );
};

export default Product;
