import CarouselItem from "./CarouselItem";

const Carousel = (props) => {
  return (
    <div
      id="productCarouselControls"
      className="carousel slide"
      data-bs-ride="carousel"
    >
      <div className="carousel-inner">
        {props.images.map((image, id) => <CarouselItem image={image} isActive={id === 0} key={id} />)}
      </div>
      <button
        className="carousel-control-prev"
        type="button"
        data-bs-target="#productCarouselControls"
        data-bs-slide="prev"
      >
        <span className="carousel-control-prev-icon" aria-hidden="true"></span>
        <span className="visually-hidden">Previous</span>
      </button>
      <button
        className="carousel-control-next"
        type="button"
        data-bs-target="#productCarouselControls"
        data-bs-slide="next"
      >
        <span className="carousel-control-next-icon" aria-hidden="true"></span>
        <span className="visually-hidden">Next</span>
      </button>
    </div>
  );
};

export default Carousel;
