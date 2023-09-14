import { useState } from "react";

const FilterItem = (props) => {
  const identifier = `Filter_` + props.type + props.id;
  const [checked, setChecked] = useState(props.checked);

  const changeHandler = (event) => {
    const value = event.target.checked;
    setChecked(value);
    props.onChange(props.id, value);
  };

  return (
    <div className="form-check">
      <input
        className="form-check-input"
        type="checkbox"
        value={props.id}
        id={identifier}
        onChange={changeHandler}
        checked={checked}
      />
      <label className="form-check-label" htmlFor={identifier}>
        {props.name}
      </label>
    </div>
  );
};

export default FilterItem;
