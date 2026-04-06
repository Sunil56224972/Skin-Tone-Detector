# SkinDetector Microservice
![Java 21](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot 3.4.4](https://img.shields.io/badge/Spring_Boot-3.4.4-brightgreen?logo=springboot)
![Docker Ready](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)
![Status](https://img.shields.io/badge/Status-Production--Ready-success)

A high-performance, Domain-Driven Design (DDD) skin detection microservice that modernizes legacy Bayesian logic with a swappable Strategy Pattern.

---

## 🚀 Getting Started

### Prerequisites
- **Java 21 (LTS)** or higher.
- **Maven 3.9+** or use the included `./mvnw`.

### Installation & Execution
```bash
# Clone the repository
git clone https://github.com/your-repo/skindetector.git
cd skindetector

# Execute using Maven
./mvnw spring-boot:run
```
The application will start on **`http://localhost:8080`**.

---

## 🛠 API Documentation

### **Detect Skin**
Analyze an image to identify skin-tone regions using either **Heuristic** or **Bayesian** algorithms.

**Endpoint**: `POST /api/v1/processor/detect-skin`

**Parameters**:
- `image` (MultipartFile): The target image (JPEG/PNG).
- `strategy` (String, Optional): `bayesian` (Default) | `heuristic`.
- `format` (String, Optional): `overlay` (Default) | `mask`.

**Success Response (200 OK)**:
- Returns binary image data (PNG).
- Includes header: `X-Skin-Percentage: [0-100]`.

#### **CURL Examples**
**1. Detect skin with overlay (Visual Highlight)**:
```bash
curl -X POST -F "image=@photo.jpg" \
     "http://localhost:8080/api/v1/processor/detect-skin?strategy=bayesian&format=overlay" \
     -o highlighted_photo.png
```

**2. Generate binary skin mask**:
```bash
curl -X POST -F "image=@photo.png" \
     "http://localhost:8080/api/v1/processor/detect-skin?format=mask" \
     -o skin_mask.png
```

---

## 📈 Scalability Roadmap

The current architecture is designed to evolve alongside increasing load and enterprise requirements.

| Phase | Description | Key Technologies |
| :--- | :--- | :--- |
| **Phase 1: Current** | Synchronous REST processing for real-time dashboard updates. | Spring Boot, Strategy Pattern. |
| **Phase 2: Distribution** | Offloading storage to Cloud-Native Object stores for high-availability. | AWS S3, Azure Blob Storage. |
| **Phase 3: Async Engine** | Decoupling analysis from the web request using a worker-queue architecture. | RabbitMQ, Redis Streams. |
| **Phase 4: ML Integration** | Replacing the Bayesian strategy with a Deep Learning inference engine. | ONNX, TensorFlow Serving. |

---

## 🐋 Deployment (Docker)
Containerize the service for one-click deployment to any Kubernetes or Cloud environment.

```bash
# Build the Docker image
docker build -t skin-detector-api .

# Run the container
docker run -p 8080:8080 skin-detector-api
```

---

## 🛡 Security & Validation
- **Strict Content-Type Check**: Rejects any file that is not `image/png` or `image/jpeg`.
- **Global Error Handling**: Standardized JSON error responses for all exceptions.
- **Stateless Design**: Ensures security through total absence of session affinity.
