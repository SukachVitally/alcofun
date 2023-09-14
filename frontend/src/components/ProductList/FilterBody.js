import FilterItem from "./FilterItem";

const FilterBody = (props) => {
  return (
    <form>
      {props.items.map((item) => (
        <FilterItem
          key={props.type + item.id}
          type={props.type}
          id={item.id}
          name={item.name}
          onChange={props.onChange}
          checked={props.enabledItems.includes(item.id)}
        />
      ))}
    </form>
  );
};

export default FilterBody;
