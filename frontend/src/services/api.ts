import type {
    ProductRecommendation,
    CustomerProfile,
    UpdateProfileResponse
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000';

// ─── Recommendations ────────────────────────────────────────────

export async function getRecommendations(
    customerId: string
): Promise<ProductRecommendation> {
    const response = await fetch(
        `${API_BASE_URL}/recommendations/${customerId}`
    );
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to get recommendations');
    }
    return response.json();
}

// ─── Customer Profile ────────────────────────────────────────────

export async function getCustomerProfile(
    customerId: string
): Promise<CustomerProfile> {
    const response = await fetch(
        `${API_BASE_URL}/customers/${customerId}/profile`
    );
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to get profile');
    }
    return response.json();
}

export async function updateCustomerProfile(
    customerId: string,
    profile: Partial<CustomerProfile>
): Promise<UpdateProfileResponse> {
    const response = await fetch(
        `${API_BASE_URL}/customers/${customerId}/profile`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profile)
        }
    );
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to update profile');
    }
    return response.json();
}

export async function createCustomerProfile(
    customerId: string,
    fullName: string,
    email: string,
    age: number
): Promise<CustomerProfile> {
    const response = await fetch(
        `${API_BASE_URL}/customers/${customerId}/profile`,
        {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ fullName, email, age })
        }
    );
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to create profile');
    }
    return response.json();
}