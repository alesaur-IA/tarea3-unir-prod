import fs from 'fs';
import http from 'http';

// Configuración
const ELASTIC_HOST = 'localhost';
const ELASTIC_PORT = 9200;
const INDEX_NAME = 'libros';

try {
    // Leemos el archivo books.json (Debe estar en la misma carpeta)
    const rawData = fs.readFileSync('books.json', 'utf8');
    const books = JSON.parse(rawData);

    console.log(`Cargando ${books.length} libros...`);

    books.forEach(book => {
        const postData = JSON.stringify(book);
        
        const options = {
            hostname: ELASTIC_HOST,
            port: ELASTIC_PORT,
            path: `/${INDEX_NAME}/_doc/${book.id}`,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': Buffer.byteLength(postData)
            }
        };

        const req = http.request(options, (res) => {
            if (res.statusCode >= 200 && res.statusCode < 300) {
                console.log(`✅ Libro ${book.id} cargado`);
            } else {
                console.log(`❌ Error libro ${book.id} status: ${res.statusCode}`);
                res.on('data', (d) => process.stdout.write(d));
            }
        });

        req.on('error', (e) => {
            console.error(`❌ Problema con la petición: ${e.message}`);
        });

        req.write(postData);
        req.end();
    });

} catch (error) {
    console.error("Error: " + error.message);
}