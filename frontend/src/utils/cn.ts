import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Utilitaire pour combiner les classes Tailwind proprement.
 * Évite les conflits de classes (ex: p-2 + p-4 → p-4 uniquement).
 */
export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}