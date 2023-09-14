const CarouselItem = (props) => {
  return (
      <div className={`carousel-item ${props.isActive ? "active" : ""}`}>
        <img src={props.image} className="d-block w-100" />
      </div>
  );
};

export default CarouselItem;
