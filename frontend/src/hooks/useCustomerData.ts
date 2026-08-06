import { useState, useEffect } from 'react';
import type { ProductRecommendation, CustomerProfile } from '../types';
import { getRecommendations, getCustomerProfile } from '../services/api';

interface UseCustomerDataResult {
    recommendations: ProductRecommendation | null;
    profile: CustomerProfile | null;
    loading: boolean;
    profileLoading: boolean;
    error: string | null;
    refresh: () => void;
    setProfile: (profile: CustomerProfile) => void;
}

export function useCustomerData(customerId: string): UseCustomerDataResult {
    const [recommendations, setRecommendations] =
        useState<ProductRecommendation | null>(null);
    const [profile, setProfile] = useState<CustomerProfile | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [profileLoading, setProfileLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const fetchRecommendations = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getRecommendations(customerId);
            setRecommendations(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Something went wrong');
        } finally {
            setLoading(false);
        }
    };

    const fetchProfile = async () => {
        try {
            setProfileLoading(true);
            const data = await getCustomerProfile(customerId);
            setProfile(data);
        } catch {
            setProfile(null);
        } finally {
            setProfileLoading(false);
        }
    };

    useEffect(() => {
        if (customerId) {
            fetchProfile();
            fetchRecommendations();
        }
    }, [customerId]);

    // useEffect(() => {
    //     const interval = setInterval(() => {
    //         if (customerId) fetchRecommendations();
    //     }, 30000);
    //     return () => clearInterval(interval);
    // }, [customerId]);

    return {
        recommendations,
        profile,
        loading,
        profileLoading,
        error,
        refresh: fetchRecommendations,
        setProfile
    };
}