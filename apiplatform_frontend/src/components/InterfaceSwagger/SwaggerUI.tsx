import React, { useEffect, useState } from 'react';
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';

const SwaggerUIComponent: React.FC<{ url: string }> = ({ url }) => {
  const [swaggerJson, setSwaggerJson] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchSwaggerJson = async () => {
      try {
        const response = await fetch(url);
        const json = await response.json();
        setSwaggerJson(json);
      } catch (error) {
        console.error('Failed to fetch Swagger JSON:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchSwaggerJson();
  }, [url]);

  if (loading) {
    return <div>加载中...</div>;
  }

  return <SwaggerUI url={swaggerJson ? url : ''} />;
};

export default SwaggerUIComponent;
