import { useState } from 'react';
import type { CustomerProfile } from '../types';
import {
    EMPLOYMENT_OPTIONS,
    INCOME_RANGES,
    RISK_OPTIONS,
    HORIZON_OPTIONS,
    INVESTMENT_GOALS,
    CRDB_PRODUCTS
} from '../types';

interface ProfileFormProps {
    currentProfile: CustomerProfile | null;
    onSave: (profile: Partial<CustomerProfile>) => void;
    saving: boolean;
}

export function ProfileForm({ currentProfile, onSave, saving }: ProfileFormProps) {
    const [employmentStatus, setEmploymentStatus] = useState(
        currentProfile?.employmentStatus || '');
    const [monthlyIncomeRange, setMonthlyIncomeRange] = useState(
        currentProfile?.monthlyIncomeRange || '');
    const [riskAppetite, setRiskAppetite] = useState(
        currentProfile?.riskAppetite || '');
    const [investmentHorizon, setInvestmentHorizon] = useState(
        currentProfile?.investmentHorizon || '');
    const [availableCapital, setAvailableCapital] = useState(
        currentProfile?.availableCapital?.toString() || '');
    const [investmentGoals, setInvestmentGoals] = useState<string[]>(
        currentProfile?.investmentGoals || []);
    const [existingProducts, setExistingProducts] = useState<string[]>(
        currentProfile?.existingProducts || []);

    const toggleItem = (list: string[], item: string, setter: (v: string[]) => void) => {
        if (list.includes(item)) {
            setter(list.filter(i => i !== item));
        } else {
            setter([...list, item]);
        }
    };

    const handleSave = () => {
        onSave({
            employmentStatus,
            monthlyIncomeRange,
            riskAppetite,
            investmentHorizon,
            availableCapital: parseFloat(availableCapital) || 0,
            investmentGoals,
            existingProducts
        });
    };

    const isValid = employmentStatus && monthlyIncomeRange &&
        riskAppetite && investmentGoals.length > 0;

    const inputStyle = {
        width: '100%',
        backgroundColor: '#f9f9f9',
        border: '1px solid #ddd',
        borderRadius: '8px',
        padding: '10px 14px',
        fontSize: '14px',
        color: '#1a1a1a',
        outline: 'none',
        boxSizing: 'border-box' as const
    };

    const labelStyle = {
        display: 'block' as const,
        color: '#444444',
        fontSize: '13px',
        fontWeight: '600' as const,
        marginBottom: '6px'
    };

    const sectionStyle = {
        marginBottom: '24px'
    };

    return (
        <div style={{
            backgroundColor: '#ffffff',
            borderRadius: '12px',
            padding: '24px',
            border: '1px solid #e8f5e9'
        }}>
            <h2 style={{
                color: '#1a7a4a',
                marginTop: 0,
                marginBottom: '6px',
                fontSize: '20px'
            }}>
                Your Financial Profile
            </h2>
            <p style={{
                color: '#666666',
                marginTop: 0,
                marginBottom: '24px',
                fontSize: '14px'
            }}>
                Complete your profile so our AI can recommend the most
                suitable CRDB products for you.
            </p>

            {/* Employment Status */}
            <div style={sectionStyle}>
                <label style={labelStyle}>Employment Status *</label>
                <select
                    value={employmentStatus}
                    onChange={e => setEmploymentStatus(e.target.value)}
                    style={inputStyle}>
                    <option value="">Select employment status</option>
                    {EMPLOYMENT_OPTIONS.map(opt => (
                        <option key={opt} value={opt}>{opt}</option>
                    ))}
                </select>
            </div>

            {/* Monthly Income Range */}
            <div style={sectionStyle}>
                <label style={labelStyle}>Monthly Income Range (TZS) *</label>
                <select
                    value={monthlyIncomeRange}
                    onChange={e => setMonthlyIncomeRange(e.target.value)}
                    style={inputStyle}>
                    <option value="">Select income range</option>
                    {INCOME_RANGES.map(opt => (
                        <option key={opt} value={opt}>{opt}</option>
                    ))}
                </select>
            </div>

            {/* Risk Appetite */}
            <div style={sectionStyle}>
                <label style={labelStyle}>Risk Appetite *</label>
                <div style={{ display: 'flex', gap: '12px' }}>
                    {RISK_OPTIONS.map(opt => (
                        <button
                            key={opt}
                            onClick={() => setRiskAppetite(opt)}
                            style={{
                                flex: 1,
                                backgroundColor: riskAppetite === opt
                                    ? '#1a7a4a' : '#f9f9f9',
                                color: riskAppetite === opt
                                    ? '#ffffff' : '#444444',
                                border: '1px solid #ddd',
                                borderRadius: '8px',
                                padding: '12px',
                                fontSize: '14px',
                                cursor: 'pointer',
                                fontWeight: riskAppetite === opt ? 'bold' : 'normal',
                                transition: 'all 0.2s ease'
                            }}>
                            {opt}
                        </button>
                    ))}
                </div>
            </div>

            {/* Investment Horizon */}
            <div style={sectionStyle}>
                <label style={labelStyle}>Investment Horizon</label>
                <select
                    value={investmentHorizon}
                    onChange={e => setInvestmentHorizon(e.target.value)}
                    style={inputStyle}>
                    <option value="">Select investment horizon</option>
                    {HORIZON_OPTIONS.map(opt => (
                        <option key={opt} value={opt}>{opt}</option>
                    ))}
                </select>
            </div>

            {/* Available Capital */}
            <div style={sectionStyle}>
                <label style={labelStyle}>Available Capital (TZS)</label>
                <input
                    type="number"
                    value={availableCapital}
                    onChange={e => setAvailableCapital(e.target.value)}
                    placeholder="e.g. 5000000"
                    style={inputStyle}
                />
            </div>

            {/* Investment Goals */}
            <div style={sectionStyle}>
                <label style={labelStyle}>Investment Goals * (select all that apply)</label>
                <div style={{
                    display: 'flex',
                    flexWrap: 'wrap',
                    gap: '8px'
                }}>
                    {INVESTMENT_GOALS.map(goal => (
                        <button
                            key={goal}
                            onClick={() => toggleItem(
                                investmentGoals, goal, setInvestmentGoals)}
                            style={{
                                backgroundColor: investmentGoals.includes(goal)
                                    ? '#1a7a4a' : '#f9f9f9',
                                color: investmentGoals.includes(goal)
                                    ? '#ffffff' : '#444444',
                                border: '1px solid #ddd',
                                borderRadius: '20px',
                                padding: '8px 16px',
                                fontSize: '13px',
                                cursor: 'pointer',
                                transition: 'all 0.2s ease'
                            }}>
                            {goal}
                        </button>
                    ))}
                </div>
            </div>

            {/* Existing Products */}
            <div style={sectionStyle}>
                <label style={labelStyle}>
                    CRDB Products You Already Hold (select all that apply)
                </label>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                    {CRDB_PRODUCTS.map(product => (
                        <button
                            key={product}
                            onClick={() => toggleItem(
                                existingProducts, product, setExistingProducts)}
                            style={{
                                backgroundColor: existingProducts.includes(product)
                                    ? '#1a4a7a' : '#f9f9f9',
                                color: existingProducts.includes(product)
                                    ? '#ffffff' : '#444444',
                                border: '1px solid #ddd',
                                borderRadius: '20px',
                                padding: '8px 16px',
                                fontSize: '13px',
                                cursor: 'pointer',
                                transition: 'all 0.2s ease'
                            }}>
                            {product}
                        </button>
                    ))}
                </div>
            </div>

            {/* Save Button */}
            <button
                onClick={handleSave}
                disabled={saving || !isValid}
                style={{
                    backgroundColor: saving || !isValid ? '#cccccc' : '#1a7a4a',
                    color: '#ffffff',
                    border: 'none',
                    borderRadius: '8px',
                    padding: '14px 24px',
                    fontSize: '15px',
                    cursor: saving || !isValid ? 'not-allowed' : 'pointer',
                    fontWeight: 'bold',
                    width: '100%',
                    transition: 'all 0.2s ease'
                }}>
                {saving ? 'Saving...' : 'Save Profile & Get Recommendations'}
            </button>

            {!isValid && (
                <p style={{
                    color: '#e53935',
                    fontSize: '13px',
                    textAlign: 'center',
                    marginTop: '8px'
                }}>
                    Please fill in all required fields marked with *
                </p>
            )}
        </div>
    );
}