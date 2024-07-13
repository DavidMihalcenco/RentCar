<div id="viewDiv" style="width: 100%; height: 900px; margin: 0; padding: 0;"></div>
<!-- Apoi, încărcați ArcGIS și scriptul dvs. -->
<script type="module">
    window.data2Show = {};
    // Import the functions you need from the SDKs you need
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js";
    import { getAnalytics } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-analytics.js";
    import { getDatabase, ref, set, onValue } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-database.js"
    // TODO: Add SDKs for Firebase products that you want to use
    // https://firebase.google.com/docs/web/setup#available-libraries

    // Your web app's Firebase configuration
    // For Firebase JS SDK v7.20.0 and later, measurementId is optional
    const firebaseConfig = {
        apiKey: "AIzaSyB7jVoYkq4zLKIryVv6cYZ8ioBanJHESsA",
        authDomain: "isibookmycar.firebaseapp.com",
        projectId: "isibookmycar",
        storageBucket: "isibookmycar.appspot.com",
        messagingSenderId: "716112652398",
        appId: "1:716112652398:web:eb41402823696f07949e7f",
        measurementId: "G-4GW2MGTHY4",
        databaseURL: "https://isibookmycar-default-rtdb.europe-west1.firebasedatabase.app"
    };

    // Initialize Firebase
    const app = initializeApp(firebaseConfig);
    const analytics = getAnalytics(app);
    const database = getDatabase(app);
    const dbRef = ref(database, 'cars/1');
    const dbRef2 = ref(database, 'cars/2');
    // global access
    // window.shared = dbRef
    /* Write to firebase
    set(dbRef, {
        test1: "test1text",
        test2: 2,
        test3: 1.23
    });
    */
    /* Read from firebase
    onValue(dbRef, (snapshot) => {
        const data = snapshot.val();
        console.log(data)
    });
    */
    onValue(dbRef, (snapshot) => {
        const data = snapshot.val();
        console.log(data)
        window.data2Show['car1'] = data;
    });
    onValue(dbRef2, (snapshot) => {
        const data = snapshot.val();
        console.log(data)
        window.data2Show['car2'] = data;
    });
</script>
<script src="https://js.arcgis.com/4.22/"></script>