import MultiRangeSlider from "multi-range-slider-react";

const PriceFilter = (props) => {
  return (
    <MultiRangeSlider
            style={{ border: "none", boxShadow: "none" }}
			min={props.min}
			max={props.max}
			minValue={props.minPrice == null ? props.min : props.minPrice}
			maxValue={props.maxPrice == null ? props.max : props.maxPrice}
            barLeftColor="grey"
            barInnerColor="blue"
            barRightColor="grey"
            ruler={false}
			onInput={(r) => {
                props.onChange({
                    "min": r.minValue == props.min ? null : r.minValue,
                    "max": r.maxValue == props.max ? null : r.maxValue
                })
			}}
		/>
  );
};

export default PriceFilter;