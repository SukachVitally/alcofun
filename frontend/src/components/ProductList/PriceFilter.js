import MultiRangeSlider from "multi-range-slider-react";
import { useState } from "react";

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
			onInput={(e) => {
                props.onChange({
                    "min": e.minValue == props.min ? null : e.minValue,
                    "max": e.maxValue == props.max ? null : e.maxValue
                })
                console.log(e)
			}}
		/>
  );
};

export default PriceFilter;