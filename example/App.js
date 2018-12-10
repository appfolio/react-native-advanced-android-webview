/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Button,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import AndroidWebView from 'react-native-advanced-android-webview';

type Props = {};
export default class App extends Component<Props> {
  state = {
    exampleToRender: null,
  }

  testFileUploads = () => (
    <AndroidWebView
      style={styles.webview}
      source={{ uri: 'https://mobilehtml5.org/ts/?id=23' }}
    />
  );

  testPDFDownloads = () => (
    <AndroidWebView
      style={styles.webview}
      source={{ uri: 'https://www.google.com/search?q=pdf+example' }}
    />
  );

  chooser = () => (
    <View>
      <Button
        title="Test File Uploads"
        onPress={() => this.setState({ exampleToRender: 'testFileUploads' })}
      />
      <Text style={styles.margin20}>or</Text>
      <Button
        title="Test PDF Donwloads"
        onPress={() => this.setState({ exampleToRender: 'testPDFDownloads' })}
      />
    </View>
  );

  render() {
    if (this.state.exampleToRender === 'testPDFDownloads') {
      return this.testPDFDownloads();
    } else if (this.state.exampleToRender === 'testFileUploads') {
      return this.testFileUploads();
    }
    return this.chooser();
  }
}

const styles = StyleSheet.create({
  webview: {
    flex: 1,
  },
  margin20: {
    margin: 20,
  },
});
