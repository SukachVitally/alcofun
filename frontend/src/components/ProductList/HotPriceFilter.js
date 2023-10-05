import { useState } from "react";

const HotPriceFilter = (props) => {
    const [hot, setHot] = useState(props.hot);

    const changeHotHandler = (event) => {
        console.log(event);
        console.log(event.target.value)
        setHot(event.target.checked);
        props.onChange(event.target.checked);
    }

    return (
        <div className="form-check">
        <input
          className="form-check-input"
          type="checkbox"
          id="hot_products"
          onChange={changeHotHandler}
          checked={hot}
        />
        <label className="form-check-label" htmlFor="hot_products">
          Only hot products
        </label>
      </div>
    )

}
export default HotPriceFilter;