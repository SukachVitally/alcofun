import { useState } from "react";
import Select from "react-select";
import Button from "react-bootstrap/Button";
import { FaArrowUp, FaArrowDown } from "react-icons/fa";
import "./Sorting.css";

const options = [
  { value: "name", label: "Name" },
  { value: "created_at", label: "Created at" },
  { value: "price", label: "Price" },
];

const ASC = "asc";
const DESC = "desc";

function Sorting(props) {
  const [selectedOption, setSelectedOption] = useState(
    props.field != null ? options.find((option) => option.value == props.field) : null
  );
  const [order, setOrder] = useState(props.order != null ? props.order : DESC);

  const fieldHandler = (option) => {
    setSelectedOption(option);
    props.onChange(option.value, order);
  };

  const orderHadler = () => {
    setOrder((prev) => {
      let value = prev == ASC ? DESC : ASC;
      if (selectedOption != null) {
        props.onChange(selectedOption.value, value);
      }
      return value;
    });
  };

  return (
    <div className="sorting_block">
      <Select
        value={selectedOption}
        onChange={fieldHandler}
        options={options}
        isSearchable={false}
      />
      <Button variant="primary" className onClick={orderHadler}>
        {order == ASC ? <FaArrowUp /> : <FaArrowDown />}
      </Button>
    </div>
  );
}

export default Sorting;
