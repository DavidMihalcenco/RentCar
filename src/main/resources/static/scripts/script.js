require(["esri/config", "esri/Map", "esri/views/MapView", "esri/widgets/Locate", "esri/widgets/Track", "esri/Graphic",
  "esri/widgets/Search", "esri/layers/FeatureLayer",
  "esri/rest/route",
  "esri/rest/support/RouteParameters",
  "esri/rest/support/FeatureSet"], function(esriConfig, Map, MapView, Locate, Track,
    Graphic, Search, FeatureLayer, route, RouteParameters, FeatureSet) {
    esriConfig.apiKey = "AAPK533501d7c41a47b3955dc3896656aadc5v3mhLzYBVxOc44jxsIQ2jJuOmONF_Ci06ypzoZyF2KcnaElPp7l3Rn2U6kF3sca";//"AAPKb624324f44944fdba86b9c10cec77f2dM492j1oJSLK4CiaVPFRZBUzNALgU864hjvo9WolDUamR_rkAkB1ATvdrOhJw_M88";
    const map = new Map({
        basemap: "arcgis-topographic" // basemap styles service
      });

      const view = new MapView({
        map: map,
        center: [-118, 34], // Longitude, latitude
        zoom: 13, // Zoom level
        container: "viewDiv" // Div element
      });

      const locate = new Locate({
        view: view,
        useHeadingEnabled: false,
        goToOverride: function(view, options) {
          options.target.scale = 1500;
          return view.goTo(options.target);
        }
      });

      view.ui.add(locate, "top-left");
      const track = new Track({
        view: view,
        graphic: new Graphic({
          symbol: {
            type: "simple-marker",
            size: "12px",
            color: "green",
            outline: {
              color: "#efefef",
              width: "1.5px"
            }
          }
        }),
        useHeadingEnabled: false
      });
      
      view.ui.add(track, "top-left");

      // Automatically start locating the user's position when the map loads
      view.when(function() {
              locate.locate();
      });

      var search = new Search({
        view: view
      });

      view.ui.add(search, {
        position: "top-right"
      });
  // Define a pop-up for Trailheads
  const popupTrailheads = {
    "title": "Point of interest:",
    "content": "<b>Name:</b> {name}<br><b>Code:</b> {code}<br><b>fclass:</b> {fclass}<br><b>osm_id:</b> {osm_id}<br>"
  }
      //Trailheads feature layer (points)
      const agencyFeatureLayer = new FeatureLayer({
        url: "https://services7.arcgis.com/GPQ8MI5SlEO3ds7W/arcgis/rest/services/puncteinteres/FeatureServer",
        outFields: ["name","code","osm_id","fclass"],
        popupTemplate: popupTrailheads
      });
      map.add(agencyFeatureLayer, 0);





  const routeUrl = "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
  view.on("click", function(event){
    if (view.graphics.length === 0) {
      addGraphic("origin", event.mapPoint);
    } else if (view.graphics.length === 1) {
      addGraphic("destination", event.mapPoint);
      getRoute(); // Call the route service
    } else {
      view.graphics.removeAll();
      addGraphic("origin",event.mapPoint);
    }
  });
  function addGraphic(type, point) {
    const graphic = new Graphic({
      symbol: {
        type: "simple-marker",
        color: (type === "origin") ? "white" : "black",
        size: "8px"
      },
      geometry: point
    });
    view.graphics.add(graphic);
  }
  function getRoute() {
    const routeParams = new RouteParameters({
      stops: new FeatureSet({
        features: view.graphics.toArray()
      }),
      returnDirections: true
    });
    route.solve(routeUrl, routeParams)
        .then(function(data) {
          data.routeResults.forEach(function(result) {
            result.route.symbol = {
              type: "simple-line",
              color: [5, 150, 255],
              width: 3
            };
            view.graphics.add(result.route);
          });
          // Display directions
          if (data.routeResults.length > 0) {
            const directions = document.createElement("ol");
            directions.classList = "esri-widget esri-widget--panel esri-directions__scroller";
            directions.style.marginTop = "0";
            directions.style.padding = "15px 15px 15px 30px";
            const features = data.routeResults[0].directions.features;
// Show each direction
            features.forEach(function(result,i){
              const direction = document.createElement("li");
              direction.innerHTML = result.attributes.text + " (" + result.attributes.length.toFixed(2) + " miles)";
              directions.appendChild(direction);
            });
            view.ui.empty("bottom-right");
            view.ui.add(directions, "bottom-right");
          }
        }).catch(function(error){
      console.log(error);
    })
  }
      // Global Access
      // console.log(window.shared)
});