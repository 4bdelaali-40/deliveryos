"""
DeliveryOS — AI Engine
FastAPI application entry point.
"""
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware

from routers import vrp, co2, eta, forecast, clustering
from utils.logger import get_logger

logger = get_logger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Startup et shutdown de l'application."""
    logger.info("DeliveryOS AI Engine starting up...")
    yield
    logger.info("DeliveryOS AI Engine shutting down...")


# ─────────────────────────────────────────
# App
# ─────────────────────────────────────────
app = FastAPI(
    title="DeliveryOS AI Engine",
    description="AI microservice — VRP, CO2, ETA, Demand forecasting, Clustering",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    lifespan=lifespan,
)

# ── Middleware ───────────────────────────
app.add_middleware(GZipMiddleware, minimum_size=1000)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)

# ── Routers ─────────────────────────────
app.include_router(vrp.router,        prefix="/api/vrp",        tags=["VRP"])
app.include_router(co2.router,        prefix="/api/co2",        tags=["CO2"])
app.include_router(eta.router,        prefix="/api/eta",        tags=["ETA"])
app.include_router(forecast.router,   prefix="/api/forecast",   tags=["Forecast"])
app.include_router(clustering.router, prefix="/api/clustering", tags=["Clustering"])


# ── Health ──────────────────────────────
@app.get("/health", tags=["Health"])
async def health():
    return {
        "status": "healthy",
        "service": "deliveryos-ai-engine",
        "version": "1.0.0",
    }