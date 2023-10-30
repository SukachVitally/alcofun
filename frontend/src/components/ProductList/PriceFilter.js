import MultiRangeSlider from "multi-range-slider-react";

const PriceFilter = (props) => {
  console.log(props);
  const min = props.priceRange.min;
  const max = props.priceRange.max;
  return (
    <MultiRangeSlider
      style={{ border: "none", boxShadow: "none" }}
      min={min}
      max={max}
      minValue={props.minPrice == null ? min : props.minPrice}
      maxValue={props.maxPrice == null ? max : props.maxPrice}
      barLeftColor="grey"
      barInnerColor="blue"
      barRightColor="grey"
      ruler={false}
      onInput={(r) => {
        props.onChange({
          min: r.minValue == min ? null : r.minValue,
          max: r.maxValue == max ? null : r.maxValue,
        });
      }}
    />
  );
};

export default PriceFilter;
