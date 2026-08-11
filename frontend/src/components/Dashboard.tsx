import { useState } from 'react';
import { useCustomerData } from '../hooks/useCustomerData';
import { RecommendationCard } from './RecommendationCard';
import { ProfileForm } from './ProfileForm';
import { updateCustomerProfile } from '../services/api';
import type { CustomerProfile } from '../types';

const DEMO_CUSTOMER_ID = 'CRDB-DEMO-001';

export function Dashboard() {
    const {
        recommendations,
        profile,
        loading,
        profileLoading,
        error,
        refresh,
        setProfile
    } = useCustomerData(DEMO_CUSTOMER_ID);

    const [activeTab, setActiveTab] = useState<'recommendations' | 'profile'>(
        'recommendations'
    );
    const [savingProfile, setSavingProfile] = useState(false);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const handleSaveProfile = async (updatedProfile: Partial<CustomerProfile>) => {
        try {
            setSavingProfile(true);
            await updateCustomerProfile(DEMO_CUSTOMER_ID, updatedProfile);
            setProfile({ ...profile, ...updatedProfile } as CustomerProfile);
            showSuccess('Profile saved! Generating fresh recommendations...');
            setActiveTab('recommendations');
            refresh();
        } catch (err) {
            showSuccess(err instanceof Error ? err.message : 'Failed to save profile');
        } finally {
            setSavingProfile(false);
        }
    };

    const showSuccess = (message: string) => {
        setSuccessMessage(message);
        setTimeout(() => setSuccessMessage(null), 4000);
    };

    const isProfileIncomplete = error && error.includes('incomplete');

    return (
        <div style={{
            minHeight: '100vh',
            backgroundColor: '#f5f9f5',
            fontFamily: 'system-ui, sans-serif'
        }}>
            {/* Header */}
            <div style={{
                backgroundColor: '#1a7a4a',
                padding: '0 32px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                height: '64px'
            }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <div style={{
                        backgroundColor: '#ffffff',
                        color: '#1a7a4a',
                        fontWeight: 'bold',
                        fontSize: '16px',
                        padding: '4px 10px',
                        borderRadius: '6px'
                    }}>
                        CRDB
                    </div>
                    <div>
                        <p style={{
                            margin: 0,
                            color: '#ffffff',
                            fontSize: '16px',
                            fontWeight: '600'
                        }}>
                            Customer Advisor
                        </p>
                        <p style={{
                            margin: 0,
                            color: '#a5d6a7',
                            fontSize: '12px'
                        }}>
                            Powered by Google Gemini AI
                        </p>
                    </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    {profile && (
                        <span style={{ color: '#a5d6a7', fontSize: '14px' }}>
                            Welcome, {profile.fullName}
                        </span>
                    )}
                    <button
                        onClick={refresh}
                        style={{
                            backgroundColor: 'rgba(255,255,255,0.15)',
                            color: '#ffffff',
                            border: '1px solid rgba(255,255,255,0.3)',
                            borderRadius: '8px',
                            padding: '8px 16px',
                            cursor: 'pointer',
                            fontSize: '13px'
                        }}>
                        Refresh
                    </button>
                </div>
            </div>

            {/* Success / Error Banner */}
            {successMessage && (
                <div style={{
                    backgroundColor: successMessage.includes('Failed') ||
                        successMessage.includes('incomplete')
                        ? '#ffebee' : '#e8f5e9',
                    color: successMessage.includes('Failed') ||
                        successMessage.includes('incomplete')
                        ? '#c62828' : '#2e7d32',
                    padding: '12px 32px',
                    fontSize: '14px',
                    fontWeight: '600',
                    borderBottom: '1px solid',
                    borderColor: successMessage.includes('Failed') ||
                        successMessage.includes('incomplete')
                        ? '#ffcdd2' : '#c8e6c9'
                }}>
                    {successMessage}
                </div>
            )}

            {/* Tab Navigation */}
            <div style={{
                backgroundColor: '#ffffff',
                borderBottom: '1px solid #e8f5e9',
                padding: '0 32px',
                display: 'flex',
                gap: '4px'
            }}>
                {(['recommendations', 'profile'] as const).map(tab => (
                    <button
                        key={tab}
                        onClick={() => setActiveTab(tab)}
                        style={{
                            backgroundColor: 'transparent',
                            color: activeTab === tab ? '#1a7a4a' : '#888888',
                            border: 'none',
                            borderBottom: activeTab === tab
                                ? '2px solid #1a7a4a'
                                : '2px solid transparent',
                            padding: '16px 20px',
                            cursor: 'pointer',
                            fontSize: '14px',
                            fontWeight: activeTab === tab ? 'bold' : 'normal',
                            textTransform: 'capitalize'
                        }}>
                        {tab === 'recommendations'
                            ? '📊 Recommendations'
                            : '👤 My Profile'}
                    </button>
                ))}
            </div>

            {/* Main Content */}
            <div style={{
                maxWidth: '800px',
                margin: '0 auto',
                padding: '32px 16px'
            }}>

                {/* Recommendations Tab */}
                {activeTab === 'recommendations' && (
                    <div>
                        <div style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            marginBottom: '24px'
                        }}>
                            <div>
                                <h2 style={{
                                    margin: 0,
                                    color: '#1a1a1a',
                                    fontSize: '22px'
                                }}>
                                    Your Product Recommendations
                                </h2>
                                {recommendations && (
                                    <p style={{
                                        margin: '4px 0 0 0',
                                        color: '#888888',
                                        fontSize: '13px'
                                    }}>
                                        Generated {new Date(
                                            recommendations.generatedAt
                                        ).toLocaleString()} · Auto refreshes every 30s
                                    </p>
                                )}
                            </div>
                        </div>

                        {/* Profile incomplete warning */}
                        {isProfileIncomplete && (
                            <div style={{
                                backgroundColor: '#fff8e1',
                                border: '1px solid #ffe082',
                                borderRadius: '12px',
                                padding: '20px 24px',
                                marginBottom: '24px'
                            }}>
                                <h3 style={{
                                    color: '#f57f17',
                                    margin: '0 0 8px 0',
                                    fontSize: '16px'
                                }}>
                                    ⚠️ Complete Your Profile First
                                </h3>
                                <p style={{
                                    color: '#795548',
                                    margin: '0 0 16px 0',
                                    fontSize: '14px'
                                }}>
                                    {error}
                                </p>
                                <button
                                    onClick={() => setActiveTab('profile')}
                                    style={{
                                        backgroundColor: '#f57f17',
                                        color: '#ffffff',
                                        border: 'none',
                                        borderRadius: '8px',
                                        padding: '10px 20px',
                                        fontSize: '14px',
                                        cursor: 'pointer',
                                        fontWeight: 'bold'
                                    }}>
                                    Complete My Profile
                                </button>
                            </div>
                        )}

                        {/* Loading state */}
                        {loading && !isProfileIncomplete && (
                            <div style={{
                                textAlign: 'center',
                                padding: '60px',
                                color: '#888888'
                            }}>
                                <p style={{ fontSize: '16px' }}>
                                    Analyzing your profile...
                                </p>
                                <p style={{ fontSize: '13px' }}>
                                    Amazon Bedrock is generating your
                                    personalized recommendations
                                </p>
                            </div>
                        )}

                        {/* Error state */}
                        {error && !isProfileIncomplete && (
                            <div style={{
                                backgroundColor: '#ffebee',
                                color: '#c62828',
                                borderRadius: '8px',
                                padding: '16px',
                                marginBottom: '16px',
                                fontSize: '14px'
                            }}>
                                {error}
                            </div>
                        )}

                        {/* Recommendations list */}
                        {!loading && !error && recommendations && (
                            <div>
                                {recommendations.products.map((product, index) => (
                                    <RecommendationCard
                                        key={index}
                                        product={product}
                                        index={index}
                                    />
                                ))}
                                <p style={{
                                    color: '#aaaaaa',
                                    fontSize: '12px',
                                    textAlign: 'center',
                                    marginTop: '24px'
                                }}>
                                    Model: {recommendations.modelUsed}
                                </p>
                            </div>
                        )}
                    </div>
                )}

                {/* Profile Tab */}
                {activeTab === 'profile' && (
                    <div>
                        {profileLoading ? (
                            <div style={{
                                textAlign: 'center',
                                padding: '60px',
                                color: '#888888'
                            }}>
                                Loading your profile...
                            </div>
                        ) : (
                            <ProfileForm
                                currentProfile={profile}
                                onSave={handleSaveProfile}
                                saving={savingProfile}
                            />
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}