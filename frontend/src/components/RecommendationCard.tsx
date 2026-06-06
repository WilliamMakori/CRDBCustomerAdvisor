import type { RecommendedProduct } from '../types';
import { CATEGORY_COLORS } from '../types';

interface RecommendationCardProps {
    product: RecommendedProduct;
    index: number;
}

export function RecommendationCard({ product, index }: RecommendationCardProps) {
    const categoryColor = CATEGORY_COLORS[product.category] || '#1a7a4a';

    return (
        <div style={{
            backgroundColor: '#ffffff',
            borderRadius: '12px',
            padding: '24px',
            marginBottom: '16px',
            border: '1px solid #e8f5e9',
            boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
            borderLeft: `4px solid ${categoryColor}`
        }}>
            {/* Header */}
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'flex-start',
                marginBottom: '12px'
            }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <div style={{
                        backgroundColor: categoryColor,
                        color: '#ffffff',
                        borderRadius: '50%',
                        width: '32px',
                        height: '32px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '14px',
                        fontWeight: 'bold',
                        flexShrink: 0
                    }}>
                        {index + 1}
                    </div>
                    <h3 style={{
                        color: '#1a1a1a',
                        margin: 0,
                        fontSize: '17px',
                        fontWeight: '600'
                    }}>
                        {product.productName}
                    </h3>
                </div>
                <span style={{
                    backgroundColor: `${categoryColor}15`,
                    color: categoryColor,
                    padding: '4px 12px',
                    borderRadius: '20px',
                    fontSize: '12px',
                    fontWeight: '600',
                    whiteSpace: 'nowrap'
                }}>
                    {product.category}
                </span>
            </div>

            {/* Reason */}
            <p style={{
                color: '#444444',
                margin: '0 0 16px 0',
                fontSize: '14px',
                lineHeight: '1.6'
            }}>
                {product.reason}
            </p>

            {/* Eligibility Note */}
            {product.eligibilityNote && (
                <div style={{
                    backgroundColor: '#fff8e1',
                    borderRadius: '8px',
                    padding: '10px 14px',
                    marginBottom: '12px',
                    display: 'flex',
                    alignItems: 'flex-start',
                    gap: '8px'
                }}>
                    <span style={{ fontSize: '14px' }}>⚠️</span>
                    <p style={{
                        color: '#795548',
                        margin: 0,
                        fontSize: '13px',
                        lineHeight: '1.5'
                    }}>
                        {product.eligibilityNote}
                    </p>
                </div>
            )}

            {/* Action to Take */}
            {product.actionToTake && (
                <div style={{
                    backgroundColor: '#e8f5e9',
                    borderRadius: '8px',
                    padding: '10px 14px',
                    marginBottom: '16px',
                    display: 'flex',
                    alignItems: 'flex-start',
                    gap: '8px'
                }}>
                    <span style={{ fontSize: '14px' }}>✅</span>
                    <p style={{
                        color: '#2e7d32',
                        margin: 0,
                        fontSize: '13px',
                        lineHeight: '1.5'
                    }}>
                        <strong>Next step:</strong> {product.actionToTake}
                    </p>
                </div>
            )}

            {/* Confidence Score */}
            <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '10px'
            }}>
                <span style={{
                    color: '#888888',
                    fontSize: '12px'
                }}>
                    AI Match Score
                </span>
                <div style={{
                    backgroundColor: '#f0f0f0',
                    borderRadius: '10px',
                    height: '6px',
                    width: '120px',
                    overflow: 'hidden'
                }}>
                    <div style={{
                        backgroundColor: categoryColor,
                        height: '100%',
                        width: `${product.confidenceScore * 100}%`,
                        borderRadius: '10px'
                    }} />
                </div>
                <span style={{
                    color: categoryColor,
                    fontSize: '12px',
                    fontWeight: '600'
                }}>
                    {Math.round(product.confidenceScore * 100)}%
                </span>
            </div>
        </div>
    );
}