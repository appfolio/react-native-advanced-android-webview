/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  View
} from 'react-native';
import AndroidWebView from 'react-native-advanced-android-webview';

type Props = {};
export default class App extends Component<Props> {
  render() {
    return (
      <AndroidWebView
        style={styles.webview}
        source={{ uri: 'https://mobilehtml5.org/ts/?id=23' }}
      />
    );
  }
}

const styles = StyleSheet.create({
  webview: {
    flex: 1,
  },
});
