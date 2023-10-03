import Carousel from "./Carousel";
import CharacteristicList from "./CharacteristicList";
import ProviderList from "./ProviderList";
import "./Product.css";
import { useState } from "react";
import StarRatings from "react-star-ratings";

const Product = (props) => {
  const product = props.product;
  const [fullDescription, setFullDescription] = useState(false);

  const descriptionClickHandler = () => {
    setFullDescription(true);
  };

  console.log(product);
  return (
    <div className="product-view">
      <h2>{product.name}</h2>
      <StarRatings
        rating={product.rating != null ? product.rating.rating : 0}
        starRatedColor="#ebd834"
        numberOfStars={5}
        starDimension={25}
        name="rating"
      />
      <p className="text-end">Barcode: {product.ean}</p>
      <Carousel images={product.imageUrls} />

      <h3>Description</h3>
      <div
        className={`product-view__description_${
          fullDescription ? "full" : "limited"
        }`}
        onClick={descriptionClickHandler}
      >
        {product.description}
      </div>
      <CharacteristicList characteristics={product.characteristics} />
      <ProviderList providers={product.providers} />
    </div>
  );
};

export default Product;
