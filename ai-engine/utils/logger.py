"""Structured logger — logging uniforme dans tout le AI engine."""
import logging
import structlog


def get_logger(name: str) -> structlog.BoundLogger:
    """Retourne un logger structuré pour le module donné."""
    logging.basicConfig(
        format="%(message)s",
        level=logging.INFO,
    )
    structlog.configure(
        processors=[
            structlog.contextvars.merge_contextvars,
            structlog.processors.add_log_level,
            structlog.processors.TimeStamper(fmt="iso"),
            structlog.dev.ConsoleRenderer(),
        ],
        wrapper_class=structlog.make_filtering_bound_logger(logging.INFO),
        context_class=dict,
        logger_factory=structlog.PrintLoggerFactory(),
    )
    return structlog.get_logger(name)