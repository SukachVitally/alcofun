import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const Graph = (props) => {

  const prepareData = (prices) => {

    const parsedData = prices.map(item => ({
      price: item.price,
      date: new Date(item.date)
    }));

    // Find the earliest and latest dates
    const startDate = parsedData[0].date;
    const endDate = Date.now();

    // Create a map of dates to prices
    const priceMap = new Map(parsedData.map(item => [item.date.toLocaleDateString(), item.price]));

    // Iterate through dates between the start and end date
    const filledData = [];
    let currentDate = new Date(startDate);
    while (currentDate <= endDate) {
      const currentDateISO = currentDate.toLocaleDateString();
      
      filledData.push({
        price: priceMap.has(currentDateISO) ? priceMap.get(currentDateISO) : null,
        date: currentDate.toLocaleDateString('en-GB') // Format date as required
      });
      currentDate.setDate(currentDate.getDate() + 1); // Move to the next day
    }

    return filledData;
  }

  const data = prepareData(props.prices)

  return <ResponsiveContainer width="100%" height={250}>
    <LineChart width={100} height={250} data={data}>
      <CartesianGrid strokeDasharray="3 3" />
      <XAxis dataKey="date" />
      <YAxis domain={['auto', 'auto']} />
      <Tooltip />
      <Line type="monotone" dot={false} dataKey="price" stroke="#8884d8" strokeWidth={3} />
    </LineChart>
  </ResponsiveContainer>
}

export default Graph