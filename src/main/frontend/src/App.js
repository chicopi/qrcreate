import './App.css';

import React from "react";
import ReactDOM from "react-dom";
import axios from 'axios'


class App extends React.PureComponent  {

  constructor(props) {
    super(props);

    this.state = {
      image:null,image2:null
     
    };
  
   
  }
  
 

  componentDidMount(){
    
    const queryParams = new URLSearchParams(window.location.search)
    const url1 = queryParams.get("urlStr")
    const logo1 = queryParams.get("thumbnail")  


    //this.setState({url: url1});
    //this.setState({logo: logo1});

    axios.get('https://qrappcreate.herokuapp.com/api/qr?urlStr=' + url1 + '&thumbnail='+ logo1).then(response => {
    //axios.get('http://localhost:8080/api/qr?urlStr=' + url1 + '&thumbnail='+ logo1).then(response => {     
      this.setState({image: response.config.url});
    });

    //axios.get('http://localhost:8080/api/qr2?urlStr=' + url1 + '&thumbnail='+ logo1).then(response => {
    //  console.log(response.data)

     
      //this.setState({image: response.data});
    //});
    
  }

  componentDidUpdate(){
    
  }

  componentWillUnmount() {
   
  }
  
  render(){

//<img alt="you dog" src={this.state.image}></img>  
//<img alt="you dog2" src={`data:image/jpeg;base64,${this.state.image2}`}></img>      

  return (
    <>   
  <div className='App'>
  <img alt="you dog2" src={this.state.image}></img>
  </div>
  </>
  )
}
}

export default App;

